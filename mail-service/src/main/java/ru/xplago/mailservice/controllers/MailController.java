package ru.xplago.mailservice.controllers;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Service;
import ru.xplago.mailservice.models.VerificationCodeKafkaModel;
import ru.xplago.mailservice.services.MailService;

@Service
public class MailController {

    private final MailService mailService;

    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("verificationCode")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @KafkaListener(id = "myId", topics = "verificationCode") //, containerFactory = "verificationCodeListener")
    public void listen(VerificationCodeKafkaModel model) throws MessagingException {
        mailService.sendVerificationCode(model.email, model.verificationCode);
    }
}
