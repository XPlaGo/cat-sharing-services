package ru.xplago.paymentservice.configuration;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.xplago.common.grpc.auth.UserServiceGrpc;
import ru.xplago.common.grpc.security.interceptors.AuthClientInterceptor;
import ru.xplago.common.grpc.transaction.TransactionServiceGrpc;

@Configuration
public class GrpcClientConfiguration {
    @GrpcClient("userService")
    private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(AuthClientInterceptor authClientInterceptor) {
        return userServiceBlockingStub.withInterceptors(authClientInterceptor);
    }

    @GrpcClient("transactionService")
    private TransactionServiceGrpc.TransactionServiceBlockingStub transactionServiceBlockingStub;

    @Bean
    public TransactionServiceGrpc.TransactionServiceBlockingStub transactionServiceBlockingStub(AuthClientInterceptor authClientInterceptor) {
        return transactionServiceBlockingStub.withInterceptors(authClientInterceptor);
    }
}
