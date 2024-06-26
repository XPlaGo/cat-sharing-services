package ru.xplago.authservice.services;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.xplago.authservice.models.VerificationCodeKafkaModel;
import org.apache.kafka.common.Uuid;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MailService {

    private KafkaTemplate<String, VerificationCodeKafkaModel> kafkaTemplate;

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("verificationCode")
                .partitions(1)
                .replicas(1)
                .build();
    }

    public void sendVerificationCode(String email, String verificationCode) {
        kafkaTemplate.send("verificationCode", Uuid.randomUuid().toString(), new VerificationCodeKafkaModel(email, verificationCode));
    }
}
