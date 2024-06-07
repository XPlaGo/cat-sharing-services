package ru.xplago.authservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.xplago.authservice.properties.VerificationCodeProperties;

@Configuration
@EnableConfigurationProperties(VerificationCodeProperties.class)
public class VerificationCodeConfig {
}
