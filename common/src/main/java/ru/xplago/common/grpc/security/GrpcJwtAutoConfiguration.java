package ru.xplago.common.grpc.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.xplago.common.grpc.security.interceptors.AuthClientInterceptor;
import ru.xplago.common.grpc.security.services.JwtService;

@Configuration
@ComponentScan
@EnableConfigurationProperties(GrpcJwtProperties.class)
public class GrpcJwtAutoConfiguration {

    private final Environment environment;
    private final GrpcJwtProperties grpcJwtProperties;

    public GrpcJwtAutoConfiguration(Environment environment, GrpcJwtProperties grpcJwtProperties) {
        this.environment = environment;
        this.grpcJwtProperties = grpcJwtProperties;
    }

    @Bean
    public JwtService jwtService() {
        return new JwtService(environment, grpcJwtProperties);
    }

    @Bean
    public AuthClientInterceptor authClientInterceptor() {
        return new AuthClientInterceptor(jwtService());
    }
}
