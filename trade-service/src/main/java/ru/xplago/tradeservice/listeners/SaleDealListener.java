package ru.xplago.tradeservice.listeners;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;
import ru.xplago.common.grpc.trade.BuyTransactionInfo;
import ru.xplago.common.kafka.models.sale.deal.*;
import ru.xplago.tradeservice.converters.SaleDealModelConverter;
import ru.xplago.tradeservice.entities.SaleDeal;
import ru.xplago.tradeservice.entities.SaleDealStatus;
import ru.xplago.tradeservice.entities.SaleOfferStatus;
import ru.xplago.tradeservice.services.SaleDealService;
import ru.xplago.tradeservice.services.SaleOfferService;
import ru.xplago.tradeservice.topics.SaleDealTopics;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SaleDealListener {
    private final ConcurrentHashMap<String, StreamObserver<BuyTransactionInfo>> observers = new ConcurrentHashMap<>();
    private final SaleDealService saleDealService;
    private final SaleOfferService saleOfferService;

    private KafkaTemplate<String, SaleDealPaymentRequestModel> saleDealPaymentRequestModelKafkaTemplate;
    private KafkaTemplate<String, SaleDealCatRequestModel> saleDealCatRequestModelKafkaTemplate;

    @Bean
    public KStream<String, SaleDealModel> joinStreams(StreamsBuilder builder) {
        JsonSerde<SaleDealPaymentResponseModel> saleDealPaymentResponseModelJsonSerde = new JsonSerde<>(SaleDealPaymentResponseModel.class);
        JsonSerde<SaleDealCatResponseModel> saleDealCatResponseModelJsonSerde = new JsonSerde<>(SaleDealCatResponseModel.class);
        JsonSerde<SaleDealModel> saleOfferModelJsonSerde = new JsonSerde<>(SaleDealModel.class);

        KStream<String, SaleDealPaymentResponseModel> paymentStream = builder
                .stream(SaleDealTopics.SALE_DEAL_PAYMENT_RESPONSE, Consumed.with(Serdes.String(), saleDealPaymentResponseModelJsonSerde));
        KStream<String, SaleDealCatResponseModel> catStream = builder
                .stream(SaleDealTopics.SALE_DEAL_CAT_RESPONSE, Consumed.with(Serdes.String(), saleDealCatResponseModelJsonSerde));

        KStream<String, SaleDealModel> resultStream = paymentStream.join(
                catStream,
                this::manageSaleOffersVerdict,
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(5)),
                StreamJoined.with(Serdes.String(), saleDealPaymentResponseModelJsonSerde, saleDealCatResponseModelJsonSerde)
        );

        resultStream.to(SaleDealTopics.SALE_DEAL, Produced.with(Serdes.String(), saleOfferModelJsonSerde));

        return resultStream;
    }

    @KafkaListener(topics = SaleDealTopics.SALE_DEAL, groupId = "trade")
    public void listenSaleOffer(ConsumerRecord<String, SaleDealModel> record) {
        String id = record.key();
        SaleDealModel model = record.value();

        SaleDeal saleDeal = saleDealService.findById(model.getId());

        if (saleDeal.getSaleOffer().getStatus() == SaleOfferStatus.CLOSED || saleDealService.existsConfirmed(saleDeal.getSaleOffer())) {
            onObserver(id, (observer) -> observer.onNext(BuyTransactionInfo.newBuilder()
                    .setStatus("REJECTED")
                    .setReason("The offer has already been closed")
                    .build()));
        } else {
            switch (model.getStatus()) {
                case CONFIRMED -> {
                    saleDeal.setTransactionId(model.getTransactionId());
                    saleDeal.getSaleOffer().setStatus(SaleOfferStatus.CLOSED);
                    saleDeal.setStatus(SaleDealStatus.CONFIRMED);
                    saleDealService.save(saleDeal);
                    saleOfferService.save(saleDeal.getSaleOffer());

                    onObserver(id, (observer) -> observer.onNext(BuyTransactionInfo.newBuilder()
                            .setStatus("CONFIRMED")
                            .build()));
                }
                case REJECTED -> {
                    if (model.getTransactionId() != null) saleDeal.setTransactionId(model.getTransactionId());
                    saleDeal.setStatus(SaleDealStatus.REJECTED);
                    saleDealService.save(saleDeal);

                    onObserver(id, (observer) -> observer.onNext(BuyTransactionInfo.newBuilder()
                            .setStatus("REJECTED")
                            .setReason(model.getReason())
                            .build()));
                }
            }
        }
        onObserver(id, StreamObserver::onCompleted);
        removeObserver(id);
    }

    public SaleDealModel manageSaleOffersVerdict(SaleDealPaymentResponseModel paymentResponseModel, SaleDealCatResponseModel catResponseModel) {

        SaleDeal saleDeal = saleDealService.findById(paymentResponseModel.getId());

        if (paymentResponseModel.getStatus() == SaleDealResponseStatus.ACCEPTED &&
                catResponseModel.getStatus() == SaleDealResponseStatus.ACCEPTED
        ) {
            return SaleDealModel.builder()
                    .id(saleDeal.getId())
                    .saleOfferId(saleDeal.getSaleOffer().getId())
                    .buyerOwnerId(saleDeal.getBuyerOwnerId())
                    .transactionId(paymentResponseModel.getTransactionId())
                    .status(SaleDealModelStatus.CONFIRMED)
                    .build();
        } else if (paymentResponseModel.getStatus() == SaleDealResponseStatus.REJECTED &&
                catResponseModel.getStatus() == SaleDealResponseStatus.REJECTED) {
            return SaleDealModel.builder()
                    .id(saleDeal.getId())
                    .saleOfferId(saleDeal.getSaleOffer().getId())
                    .buyerOwnerId(saleDeal.getBuyerOwnerId())
                    .status(SaleDealModelStatus.REJECTED)
                    .reason(paymentResponseModel.getReason() + "; " + catResponseModel.getReason())
                    .build();
        } else if (paymentResponseModel.getStatus() == SaleDealResponseStatus.ACCEPTED &&
                catResponseModel.getStatus() == SaleDealResponseStatus.REJECTED) {

            SaleDealPaymentRequestModel model = SaleDealModelConverter.toPaymentRequest(
                    saleDeal,
                    paymentResponseModel.getSellerPaymentAccountId(),
                    paymentResponseModel.getBuyerPaymentAccountId(),
                    SaleDealRequestAction.ROLLBACK
            );
            model.setTransactionId(paymentResponseModel.getTransactionId());
            saleDeal.setTransactionId(paymentResponseModel.getTransactionId());

            saleDealPaymentRequestModelKafkaTemplate.send(SaleDealTopics.SALE_DEAL_PAYMENT_REQUEST, Uuid.randomUuid().toString(), model);

            SaleDealModel saleDealModel = SaleDealModelConverter.toModel(saleDeal, SaleDealModelStatus.REJECTED);
            saleDealModel.setReason(catResponseModel.getReason());
            return saleDealModel;
        } else if (paymentResponseModel.getStatus() == SaleDealResponseStatus.REJECTED &&
                catResponseModel.getStatus() == SaleDealResponseStatus.ACCEPTED) {

            SaleDealCatRequestModel model = SaleDealModelConverter.toCatRequest(saleDeal, SaleDealRequestAction.ROLLBACK);

            saleDealCatRequestModelKafkaTemplate.send(SaleDealTopics.SALE_DEAL_CAT_REQUEST, Uuid.randomUuid().toString(), model);

            SaleDealModel saleDealModel = SaleDealModelConverter.toModel(saleDeal, SaleDealModelStatus.REJECTED);
            saleDealModel.setReason(paymentResponseModel.getReason());
            return saleDealModel;
        }
        return null;
    }

    public void addObserver(String id, StreamObserver<BuyTransactionInfo> observer) {
        observers.put(id, observer);
    }

    public void removeObserver(String id) {
        observers.remove(id);
    }

    public boolean observerExist(String id) {
        return observers.containsKey(id);
    }

    public StreamObserver<BuyTransactionInfo> getObserver(String id) {
        return observers.get(id);
    }

    public void onObserver(String id, Consumer<StreamObserver<BuyTransactionInfo>> function) {
        StreamObserver<BuyTransactionInfo> observer = getObserver(id);
        if (observer != null) {
            function.accept(observer);
        }
    }
}
