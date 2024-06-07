package ru.xplago.tradeservice.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class SaleDealTopics {

    public static final String SALE_DEAL = "saleOffer";

    @Bean
    public NewTopic saleDealTopic() {
        return TopicBuilder.name(SALE_DEAL)
                .partitions(1)
                .replicas(1)
                .build();
    }

    public static final String SALE_DEAL_PAYMENT_REQUEST = "saleOfferPaymentRequest";

    @Bean
    public NewTopic saleDealPaymentRequestTopic() {
        return TopicBuilder.name(SALE_DEAL_PAYMENT_REQUEST)
                .partitions(1)
                .replicas(1)
                .build();
    }

    public static final String SALE_DEAL_PAYMENT_RESPONSE = "saleOfferPaymentResponse";

    @Bean
    public NewTopic saleDealPaymentResponseTopic() {
        return TopicBuilder.name(SALE_DEAL_PAYMENT_RESPONSE)
                .partitions(1)
                .replicas(1)
                .build();
    }

    public static final String SALE_DEAL_CAT_REQUEST = "saleOfferCatRequest";

    @Bean
    public NewTopic saleDealCatRequestTopic() {
        return TopicBuilder.name(SALE_DEAL_CAT_REQUEST)
                .partitions(1)
                .replicas(1)
                .build();
    }

    public static final String SALE_DEAL_CAT_RESPONSE = "saleOfferCatResponse";

    @Bean
    public NewTopic saleDealCatResponseTopic() {
        return TopicBuilder.name(SALE_DEAL_CAT_RESPONSE)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
