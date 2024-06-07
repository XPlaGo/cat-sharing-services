package ru.xplago.authservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@RedisHash("VerificationCode")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerificationCodeModel {
    @Id
    private String email;
    private String verificationCode;
    private Date expiryDate;
    private Date createDate;
}
