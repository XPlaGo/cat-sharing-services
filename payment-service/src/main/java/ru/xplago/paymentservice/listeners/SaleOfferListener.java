package ru.xplago.paymentservice.listeners;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.Uuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.xplago.common.kafka.models.sale.deal.SaleDealPaymentRequestModel;
import ru.xplago.common.kafka.models.sale.deal.SaleDealPaymentResponseModel;
import ru.xplago.common.kafka.models.sale.deal.SaleDealResponseStatus;
import ru.xplago.common.kafka.models.transaction.TransactionModel;
import ru.xplago.common.kafka.models.transaction.TransactionModelStatus;
import ru.xplago.paymentservice.exceptions.NotFoundException;
import ru.xplago.paymentservice.exceptions.TransactionException;
import ru.xplago.paymentservice.exceptions.TransactionValidationException;
import ru.xplago.paymentservice.services.PaymentService;
import ru.xplago.paymentservice.topics.SaleOfferTopics;
import ru.xplago.paymentservice.topics.TransactionTopics;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class SaleOfferListener {

    private final PaymentService paymentService;
    private KafkaTemplate<String, SaleDealPaymentResponseModel> saleDealPaymentResponseModelKafkaTemplate;
    private final KafkaTemplate<String, TransactionModel> transactionModelKafkaTemplate;

    @KafkaListener(id = "saleOfferPaymentRequest", topics = "saleOfferPaymentRequest", groupId = "payment")
    public void listenSaleOffers(SaleDealPaymentRequestModel saleDealPaymentRequestModel) {
        switch (saleDealPaymentRequestModel.getAction()) {
            case NEW -> {
                SaleDealPaymentResponseModel model;
                try {
                    model = transfer(saleDealPaymentRequestModel);
                } catch (Exception exception) {
                    log.error(exception.getMessage(), exception);
                    model = SaleDealPaymentResponseModel.builder()
                            .id(saleDealPaymentRequestModel.getId())
                            .saleOfferId(saleDealPaymentRequestModel.getSaleOfferId())
                            .buyerPaymentAccountId(saleDealPaymentRequestModel.getBuyerPaymentAccountId())
                            .sellerPaymentAccountId(saleDealPaymentRequestModel.getSellerPaymentAccountId())
                            .status(SaleDealResponseStatus.REJECTED)
                            .reason(exception.getMessage())
                            .build();
                }
                saleDealPaymentResponseModelKafkaTemplate.send(SaleOfferTopics.SALE_OFFER_PAYMENT_RESPONSE, model.getId(), model);
            }
            case ROLLBACK -> {
                try {
                    rollback(saleDealPaymentRequestModel);
                } catch (Exception exception) {
                    log.error(exception.getMessage(), exception);
                }
            }
        }
    }

    public SaleDealPaymentResponseModel transfer(SaleDealPaymentRequestModel saleDealPaymentRequestModel) {
        Uuid transactionId = Uuid.randomUuid();

        TransactionModel transactionModel = TransactionModel.builder()
                .id(transactionId.toString())
                .amount(saleDealPaymentRequestModel.getPrice())
                .senderAccountId(saleDealPaymentRequestModel.getBuyerPaymentAccountId())
                .receiverAccountId(saleDealPaymentRequestModel.getSellerPaymentAccountId())
                .comment("Payment for the purchase of a sale order")
                .status(TransactionModelStatus.PENDING.name())
                .created(Timestamp.from(Instant.now()))
                .modified(Timestamp.from(Instant.now()))
                .build();

        String transactionRejectedReason = null;
        try {
            transactionModel = paymentService.transfer(transactionModel);
        } catch (TransactionValidationException | TransactionException | NotFoundException exception) {
            transactionModel.setStatus(TransactionModelStatus.REJECTED.name());
            transactionRejectedReason = exception.getMessage();
        }

        transactionModelKafkaTemplate.send(TransactionTopics.TRANSACTION_TOPIC, transactionModel.getId(), transactionModel);

        SaleDealResponseStatus status;
        switch (transactionModel.getStatus()) {
            case "SUCCESS" -> status = SaleDealResponseStatus.ACCEPTED;
            default -> status = SaleDealResponseStatus.REJECTED;
        }

        SaleDealPaymentResponseModel.SaleDealPaymentResponseModelBuilder builder =  SaleDealPaymentResponseModel.builder()
                .id(saleDealPaymentRequestModel.getId())
                .saleOfferId(saleDealPaymentRequestModel.getSaleOfferId())
                .transactionId(transactionModel.getId())
                .buyerPaymentAccountId(saleDealPaymentRequestModel.getBuyerPaymentAccountId())
                .sellerPaymentAccountId(saleDealPaymentRequestModel.getSellerPaymentAccountId())
                .status(status);

        if (transactionRejectedReason != null) builder.reason(transactionRejectedReason);

        return builder.build();
    }

    public void rollback(SaleDealPaymentRequestModel saleDealPaymentRequestModel) {
        TransactionModel transactionModel = paymentService.rollback(saleDealPaymentRequestModel.getTransactionId());
        transactionModelKafkaTemplate.send(TransactionTopics.TRANSACTION_TOPIC, Uuid.randomUuid().toString(), transactionModel);
    }
}
