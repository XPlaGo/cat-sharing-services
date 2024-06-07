package ru.xplago.common.grpc.security.data;

import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class JwtContextData {
    private final String jwt;
    private final String userId;
    private final Set<String> roles;
    private final Claims jwtClaims;
}
