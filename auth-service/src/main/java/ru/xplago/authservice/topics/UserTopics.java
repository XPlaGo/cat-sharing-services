package ru.xplago.authservice.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class UserTopics {
    public static String USER_ACTION = "userAction";

    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name(USER_ACTION)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
