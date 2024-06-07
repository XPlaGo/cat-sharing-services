package ru.xplago.tradeservice.configuration;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.xplago.common.grpc.cat.CatServiceGrpc;
import ru.xplago.common.grpc.cat.OwnerServiceGrpc;
import ru.xplago.common.grpc.payment.PaymentAccountServiceGrpc;
import ru.xplago.common.grpc.security.interceptors.AuthClientInterceptor;

@Configuration
public class GrpcClientConfiguration {
    @GrpcClient("catService")
    private OwnerServiceGrpc.OwnerServiceBlockingStub ownerServiceBlockingStub;

    @Bean
    public OwnerServiceGrpc.OwnerServiceBlockingStub ownerServiceBlockingStub(AuthClientInterceptor authClientInterceptor) {
        return ownerServiceBlockingStub.withInterceptors(authClientInterceptor);
    }

    @GrpcClient("catService")
    private CatServiceGrpc.CatServiceBlockingStub catServiceBlockingStub;

    @Bean
    public CatServiceGrpc.CatServiceBlockingStub catServiceBlockingStub(AuthClientInterceptor authClientInterceptor) {
        return catServiceBlockingStub.withInterceptors(authClientInterceptor);
    }

    @GrpcClient("paymentService")
    private PaymentAccountServiceGrpc.PaymentAccountServiceBlockingStub paymentAccountServiceBlockingStub;

    @Bean
    public PaymentAccountServiceGrpc.PaymentAccountServiceBlockingStub paymentAccountServiceBlockingStub(AuthClientInterceptor authClientInterceptor) {
        return paymentAccountServiceBlockingStub.withInterceptors(authClientInterceptor);
    }
}
