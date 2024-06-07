package ru.xplago.mailservice.models;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;

@AllArgsConstructor

public class VerificationCodeKafkaModel extends StringSerializer {
    public String email;
    public String verificationCode;
}
