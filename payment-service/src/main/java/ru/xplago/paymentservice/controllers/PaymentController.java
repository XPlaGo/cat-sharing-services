package ru.xplago.paymentservice.controllers;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.kafka.common.Uuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import ru.xplago.common.grpc.payment.PaymentServiceGrpc;
import ru.xplago.common.grpc.payment.TransactionInfo;
import ru.xplago.common.grpc.payment.TransferRequest;
import ru.xplago.common.grpc.security.resolvers.UserIdResolver;
import ru.xplago.common.grpc.transaction.TransactionDataInfo;
import ru.xplago.common.kafka.models.transaction.TransactionModel;
import ru.xplago.common.kafka.models.transaction.TransactionModelStatus;
import ru.xplago.paymentservice.converters.MoneyConverter;
import ru.xplago.paymentservice.entities.PaymentAccount;
import ru.xplago.paymentservice.exceptions.NotFoundException;
import ru.xplago.paymentservice.exceptions.TransactionException;
import ru.xplago.paymentservice.exceptions.TransactionValidationException;
import ru.xplago.paymentservice.services.PaymentAccountService;
import ru.xplago.paymentservice.services.PaymentService;
import ru.xplago.paymentservice.services.TransactionService;
import ru.xplago.paymentservice.topics.TransactionTopics;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@GrpcService
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentController extends PaymentServiceGrpc.PaymentServiceImplBase {
    private final PaymentAccountService paymentAccountService;
    private final PaymentService paymentService;
    private final TransactionService transactionService;

    private final KafkaTemplate<String, TransactionModel> transactionModelKafkaTemplate;

    @Override
    public void transfer(TransferRequest request, StreamObserver<TransactionInfo> responseObserver) {

        Long userId = UserIdResolver.resolve();

        Uuid transactionId = Uuid.randomUuid();

        BigDecimal amount = MoneyConverter.convert(request.getAmount());

        TransactionModel transactionModel = TransactionModel.builder()
                .id(transactionId.toString())
                .amount(amount)
                .senderAccountId(request.getSenderAccountId())
                .receiverAccountId(request.getReceiverAccountId())
                .comment(request.getComment())
                .status(TransactionModelStatus.PENDING.name())
                .created(Timestamp.from(Instant.now()))
                .modified(Timestamp.from(Instant.now()))
                .build();

        responseObserver.onNext(
                TransactionInfo.newBuilder()
                        .setId(transactionModel.getId())
                        .setStatus(transactionModel.getStatus())
                        .build()
        );

        TransactionModel resultTransactionModel;
        try {
            resultTransactionModel = paymentService.transfer(transactionModel);
        } catch (TransactionValidationException | TransactionException | NotFoundException exception) {
            onTransactionExceptionHandler(responseObserver, transactionModel);
            return;
        }

        TransactionInfo transactionInfo = TransactionInfo.newBuilder()
                .setId(resultTransactionModel.getId())
                .setStatus(resultTransactionModel.getStatus())
                .build();

        responseObserver.onNext(transactionInfo);

        transactionModelKafkaTemplate.send(TransactionTopics.TRANSACTION_TOPIC, resultTransactionModel.getId(), resultTransactionModel);

        responseObserver.onCompleted();
    }

    private void onTransactionExceptionHandler(StreamObserver<TransactionInfo> responseObserver, TransactionModel transactionModel) {
        transactionModel.setStatus(TransactionModelStatus.REJECTED.name());
        TransactionInfo transactionInfo = TransactionInfo.newBuilder()
                .setId(transactionModel.getId())
                .setStatus(transactionModel.getStatus())
                .build();

        responseObserver.onNext(transactionInfo);

        transactionModelKafkaTemplate.send(TransactionTopics.TRANSACTION_TOPIC, transactionInfo.getId(), transactionModel);

        responseObserver.onCompleted();
    }

    @Override
    public void rollback(StringValue request, StreamObserver<Empty> responseObserver) {
        Long userId = UserIdResolver.resolve();

        TransactionDataInfo transactionDataInfo = transactionService.getTransactionById(request.getValue());
        PaymentAccount paymentAccount = paymentAccountService.findByIdAndUserId(transactionDataInfo.getSenderAccountId(), userId);

        if (paymentAccount == null) {
            throw new TransactionException("Cannot rollback transaction");
        }

        TransactionModel transactionModel = paymentService.rollback(transactionDataInfo.getId());
        transactionModelKafkaTemplate.send(TransactionTopics.TRANSACTION_TOPIC, Uuid.randomUuid().toString(), transactionModel);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
