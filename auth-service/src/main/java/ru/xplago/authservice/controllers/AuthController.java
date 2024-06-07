package ru.xplago.authservice.controllers;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.xplago.authservice.converters.UserInfoConverter;
import ru.xplago.authservice.models.VerificationCodeModel;
import ru.xplago.authservice.services.AuthRole;
import ru.xplago.authservice.services.AuthService;
import ru.xplago.authservice.services.UserService;
import ru.xplago.authservice.services.VerificationCodeService;
import ru.xplago.authservice.services.dto.SignInDto;
import ru.xplago.authservice.services.dto.SignUpDto;
import ru.xplago.authservice.services.dto.TokenWithUserDto;
import ru.xplago.common.grpc.auth.*;
import ru.xplago.common.grpc.security.annotations.Allow;
import ru.xplago.common.grpc.security.exceptions.UnauthenticatedException;
import ru.xplago.common.grpc.security.services.JwtService;
import ru.xplago.common.grpc.security.services.dto.JwtData;

import java.time.Instant;

@Slf4j
@GrpcService
@AllArgsConstructor(onConstructor_ = @__({@Autowired}))
public class AuthController extends AuthServiceGrpc.AuthServiceImplBase {

    private UserService userService;
    private AuthService authService;
    private VerificationCodeService verificationCodeService;
    private JwtService jwtService;

    @Override
    public void sendEmail(SendEmailRequest sendEmailRequest, StreamObserver<PlainTokenResponse> responseObserver) {
        VerificationCodeModel model = verificationCodeService
                .generateAndSendVerificationCode(sendEmailRequest.getEmail());

        String accessToken = jwtService.generate(new JwtData(model.getEmail(), AuthRole.TEMPORARY));
        PlainTokenResponse response = PlainTokenResponse.newBuilder()
                .setAccessToken(accessToken)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Allow(roles = AuthRole.TEMPORARY)
    @Override
    public void sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest, StreamObserver<VerifiedTokenResponse> responseObserver) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        VerificationCodeModel model = verificationCodeService
                .checkAndDeleteVerificationCode(authentication.getName(), sendVerificationCodeRequest.getVerificationCode());

        String accessToken = jwtService.generate(new JwtData(model.getEmail(), AuthRole.VERIFIED));

        boolean userExists = userService.existsByEmail(authentication.getName());

        VerifiedTokenResponse response = VerifiedTokenResponse.newBuilder()
                .setAccessToken(accessToken)
                .setUserExists(userExists)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Allow(roles = AuthRole.VERIFIED)
    @Override
    public void signIn(SignInRequest signInRequest, StreamObserver<TokenWithUserResponse> responseObserver) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        TokenWithUserDto dto = authService.signIn(new SignInDto(
                authentication.getName(),
                signInRequest.getPassword()
        ));

        UserInfo userInfo = UserInfoConverter.convert(dto.getUserInfo());
        responseObserver.onNext(
                TokenWithUserResponse.newBuilder()
                        .setAccessToken(dto.getAccessToken())
                        .setUserInfo(userInfo)
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Allow(roles = AuthRole.VERIFIED)
    @Override
    public void signUp(SignUpRequest signUpRequest, StreamObserver<TokenWithUserResponse> responseObserver) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        TokenWithUserDto dto = authService.signUp(new SignUpDto(
                authentication.getName(),
                signUpRequest.getName(),
                signUpRequest.getPassword(),
                signUpRequest.getBirthday().getSeconds() == 0 ? null :
                        java.sql.Timestamp.from(
                            Instant.ofEpochSecond(
                                    signUpRequest.getBirthday().getSeconds(),
                                    signUpRequest.getBirthday().getNanos())
                    ),
                signUpRequest.getAvatar()
        ));

        UserInfo userInfo = UserInfoConverter.convert(dto.getUserInfo());
        responseObserver.onNext(
                TokenWithUserResponse.newBuilder()
                        .setAccessToken(dto.getAccessToken())
                        .setUserInfo(userInfo)
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    public void refresh(Empty empty, StreamObserver<TokenWithUserResponse> responseObserver) {
        Long userId = getUserId();

        TokenWithUserDto dto = authService.refresh(userId);

        UserInfo userInfo = UserInfoConverter.convert(dto.getUserInfo());
        responseObserver.onNext(
                TokenWithUserResponse.newBuilder()
                        .setAccessToken(dto.getAccessToken())
                        .setUserInfo(userInfo)
                        .build()
        );
        responseObserver.onCompleted();
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new UnauthenticatedException("Invalid user id passed in access token", e);
        }
    }
}
