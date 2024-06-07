package ru.xplago.transactionservice.listeners;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;
import ru.xplago.transactionservice.converters.TransactionModelConverter;
import ru.xplago.common.kafka.models.transaction.TransactionModel;
import ru.xplago.transactionservice.services.TransactionService;
import ru.xplago.transactionservice.topics.TransactionTopics;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionListener {
    private TransactionService transactionService;

    private void observeTransaction(String id, TransactionModel transaction) {
        transactionService.save(TransactionModelConverter.convert(transaction));
    }

    @Bean
    public KStream<String, TransactionModel> transactionStream(StreamsBuilder builder) {
        JsonSerde<TransactionModel> transactionSerde = new JsonSerde<>(TransactionModel.class);
        KStream<String, TransactionModel> stream = builder
                .stream(TransactionTopics.TRANSACTION_TOPIC, Consumed.with(Serdes.String(), transactionSerde));

        stream.foreach(this::observeTransaction);
        return stream;
    }
}
