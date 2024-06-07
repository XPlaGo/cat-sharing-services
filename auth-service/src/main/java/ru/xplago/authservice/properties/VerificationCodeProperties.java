package ru.xplago.authservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "security.verification-code")
public class VerificationCodeProperties {
    private Long expirationSec = 120L;
    private Long refreshSec = 60L;
}
