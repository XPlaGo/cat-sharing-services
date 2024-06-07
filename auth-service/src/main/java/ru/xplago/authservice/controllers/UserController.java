package ru.xplago.authservice.controllers;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import ru.xplago.authservice.converters.UserInfoConverter;
import ru.xplago.authservice.entities.User;
import ru.xplago.authservice.services.UserService;
import ru.xplago.common.grpc.auth.GetUserByEmailRequest;
import ru.xplago.common.grpc.auth.UserInfo;
import ru.xplago.common.grpc.auth.UserServiceGrpc;
import ru.xplago.common.grpc.security.annotations.Allow;
import ru.xplago.common.grpc.security.services.GrpcRole;

@GrpcService
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController extends UserServiceGrpc.UserServiceImplBase {

    private UserService userService;

    @Allow(roles = {GrpcRole.INTERNAL, "ROLE_USER", "ROLE_ADMIN"})
    @Override
    public void getUserByEmail(GetUserByEmailRequest request, StreamObserver<UserInfo> responseObserver) {
        User user = userService.findByEmail(request.getEmail());

        responseObserver.onNext(UserInfoConverter.convert(user));
        responseObserver.onCompleted();
    }
}
