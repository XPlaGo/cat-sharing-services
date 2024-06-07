package ru.xplago.paymentservice.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class SaleOfferTopics {

    public static String SALE_OFFER_PAYMENT_REQUEST = "saleOfferPaymentRequest";

    @Bean
    public NewTopic saleOfferPaymentRequestTopic() {
        return TopicBuilder.name(SALE_OFFER_PAYMENT_REQUEST)
                .partitions(1)
                .replicas(1)
                .build();
    }

    public static String SALE_OFFER_PAYMENT_RESPONSE = "saleOfferPaymentResponse";

    @Bean
    public NewTopic saleOfferPaymentResponseTopic() {
        return TopicBuilder.name(SALE_OFFER_PAYMENT_RESPONSE)
                .partitions(1)
                .replicas(1)
                .build();
    }

    public static String SALE_OFFER_CAT_REQUEST = "saleOfferCatRequest";

    @Bean
    public NewTopic saleOfferCatRequestTopic() {
        return TopicBuilder.name(SALE_OFFER_CAT_REQUEST)
                .partitions(1)
                .replicas(1)
                .build();
    }

    public static String SALE_OFFER_CAT_RESPONSE = "saleOfferCatResponse";

    @Bean
    public NewTopic saleOfferCatResponseTopic() {
        return TopicBuilder.name(SALE_OFFER_CAT_RESPONSE)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
