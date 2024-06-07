package ru.xplago.authservice.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class VerificationCodeKafkaModel {
    public String email;
    public String verificationCode;
}
