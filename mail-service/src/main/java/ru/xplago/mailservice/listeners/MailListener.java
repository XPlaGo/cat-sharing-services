package ru.xplago.mailservice.listeners;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import ru.xplago.mailservice.models.VerificationCodeKafkaModel;
import ru.xplago.mailservice.services.MailService;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;

import ru.xplago.mailservice.topics.VerificationCodeTopics;

@Configuration
public class MailListener {

    @Autowired
    public MailListener(MailService mailService) {
        this.mailService = mailService;
    }

    private final MailService mailService;

    private void observeVerificationCode(String id, VerificationCodeKafkaModel model) {
        try {
            mailService.sendVerificationCode(model.email, model.verificationCode);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public KStream<String, VerificationCodeKafkaModel> transactionStream(StreamsBuilder builder) {
        JsonSerde<VerificationCodeKafkaModel> verificationCodeSerde = new JsonSerde<>(VerificationCodeKafkaModel.class);
        KStream<String, VerificationCodeKafkaModel> stream = builder
                .stream(VerificationCodeTopics.VERIFICATION_CODE, Consumed.with(Serdes.String(), verificationCodeSerde));

        stream.foreach(this::observeVerificationCode);
        return stream;
    }
}