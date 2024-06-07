package ru.xplago.transactionservice.configuration;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.xplago.common.grpc.payment.PaymentAccountServiceGrpc;
import ru.xplago.common.grpc.security.interceptors.AuthClientInterceptor;

@Configuration
public class GrpcClientConfiguration {
    @GrpcClient("paymentService")
    private PaymentAccountServiceGrpc.PaymentAccountServiceBlockingStub stub;

    @Bean
    public PaymentAccountServiceGrpc.PaymentAccountServiceBlockingStub userServiceBlockingStub(AuthClientInterceptor authClientInterceptor) {
        return stub.withInterceptors(authClientInterceptor);
    }
}
