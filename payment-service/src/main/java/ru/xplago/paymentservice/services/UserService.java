package ru.xplago.paymentservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.common.grpc.auth.GetUserByEmailRequest;
import ru.xplago.common.grpc.auth.UserInfo;
import ru.xplago.common.grpc.auth.UserServiceGrpc;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {
    private final UserServiceGrpc.UserServiceBlockingStub stub;

    public UserInfo getUserByEmail(String email) {
        return stub.getUserByEmail(
                GetUserByEmailRequest.newBuilder()
                        .setEmail(email)
                        .build()
        );
    }
}
