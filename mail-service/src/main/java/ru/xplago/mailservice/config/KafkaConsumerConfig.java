package ru.xplago.mailservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.xplago.mailservice.models.VerificationCodeKafkaModel;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
//    @Bean
//    public ConsumerFactory<String, VerificationCodeKafkaModel> verificationCodeConsumer() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//
//        return new DefaultKafkaConsumerFactory<>(
//                props, new StringDeserializer(),
//                new JsonDeserializer<VerificationCodeKafkaModel>(VerificationCodeKafkaModel.class, false)
//        );
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, VerificationCodeKafkaModel> verificationCodeListener() {
//        ConcurrentKafkaListenerContainerFactory<String, VerificationCodeKafkaModel> factory= new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(verificationCodeConsumer());
//        return factory;
//    }
}
