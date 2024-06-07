package ru.xplago.common.grpc.security.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.xplago.common.grpc.security.GrpcJwtProperties;
import ru.xplago.common.grpc.security.services.dto.JwtData;
import ru.xplago.common.grpc.security.services.dto.JwtMetadata;
import ru.xplago.common.grpc.security.services.dto.JwtToken;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {

    public static final String TOKEN_ENV = "token_env";
    public static final String JWT_ROLES = "jwt_roles";

    private static final String INTERNAL_ACCOUNT = "internal_account";
    private static final Double REFRESH_TIME_THRESHOLD = 0.2;

    private final GrpcJwtProperties properties;

    private JwtMetadata metadata;
    private JwtToken internal;

    public JwtService(Environment env, GrpcJwtProperties properties) {
        this.properties = properties;
        this.metadata = JwtMetadata.builder()
                .env(Arrays.stream(env.getActiveProfiles()).collect(Collectors.toList()))
                .expirationSec(properties.getExpirationSec())
                .key(generateKey(properties.getSecret(), properties.getAlgorithm()))
                .build();
        this.internal = generateInternalToken(properties.getExpirationSec(), metadata);
    }

    public String generate(JwtData data) {
        return generateJwt(data, metadata);
    }

    public String getInternal() {
        final double refreshThresholdValue = properties.getExpirationSec() * REFRESH_TIME_THRESHOLD;

        if (LocalDateTime.now().plusSeconds((long) refreshThresholdValue).isAfter(internal.getExpiration())) {
            refreshInternalToken();
        }

        return internal.getToken();
    }

    public SecretKey getKey() {
        return metadata.getKey();
    }

    private SecretKeySpec generateKey(String signingSecret, String signAlgorithm) {
        final String sha256hex = DigestUtils.sha256Hex(signingSecret);
        final byte[] decodedKey = Base64.getDecoder().decode(sha256hex);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, signAlgorithm);
    }

    private String generateJwt(JwtData data, JwtMetadata metadata) {
        final LocalDateTime future = LocalDateTime.now().plusSeconds(metadata.getExpirationSec());
        final Claims ourClaims = Jwts.claims();

        ourClaims.put(JWT_ROLES, Lists.newArrayList(data.getRoles()));
        ourClaims.put(TOKEN_ENV, metadata.getEnv());

        return Jwts.builder()
                .setClaims(ourClaims)
                .setSubject(data.getUserId())
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(future.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(metadata.getKey()).compact();
    }

    private void refreshInternalToken() {
        this.internal = generateInternalToken(properties.getExpirationSec(), metadata);
    }

    private JwtToken generateInternalToken(Long expirationSec, JwtMetadata jwtMetadata) {
        return new JwtToken(
                generateJwt(new JwtData(INTERNAL_ACCOUNT, Sets.newHashSet(GrpcRole.INTERNAL)), jwtMetadata),
                LocalDateTime.now().plusSeconds(expirationSec)
        );
    }
}
