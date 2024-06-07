package ru.xplago.paymentservice.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.Uuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.common.grpc.transaction.TransactionDataInfo;
import ru.xplago.common.kafka.models.transaction.TransactionModel;
import ru.xplago.common.kafka.models.transaction.TransactionModelStatus;
import ru.xplago.paymentservice.converters.MoneyConverter;
import ru.xplago.paymentservice.entities.PaymentAccount;
import ru.xplago.paymentservice.exceptions.TransactionException;
import ru.xplago.paymentservice.exceptions.TransactionValidationException;
import ru.xplago.paymentservice.repositories.PaymentAccountRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentService {
    private PaymentAccountRepository paymentAccountRepository;
    private TransactionService transactionService;

    @Transactional
    public TransactionModel transfer(TransactionModel transactionModel) {
        PaymentAccount senderAccount = paymentAccountRepository.findById(
                transactionModel.getSenderAccountId()
        ).orElseThrow(() -> new TransactionValidationException("Invalid sender account id", "senderAccountId", "Invalid account id"));

        PaymentAccount receiverAccount = paymentAccountRepository.findById(
                transactionModel.getReceiverAccountId()
        ).orElseThrow(() -> new TransactionValidationException("Invalid receiver account id", "receiverAccountId", "Invalid account id"));

        if (senderAccount.getId().equals(receiverAccount.getId())) {
            throw  new TransactionValidationException("Invalid receiver account id", "receiverAccountId", "Invalid account id");
        }

        if (transactionModel.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionValidationException("Invalid amount", "amount", "Amount value must be greater than zero");
        }

        if (senderAccount.getAmount().compareTo(transactionModel.getAmount()) < 0) {
            throw new TransactionValidationException("Not enough money", "amount", "Not enough money");
        }

        Uuid transactionId = Uuid.randomUuid();

        senderAccount.setAmount(senderAccount.getAmount().subtract(transactionModel.getAmount()));
        receiverAccount.setAmount(receiverAccount.getAmount().add(transactionModel.getAmount()));

        paymentAccountRepository.save(senderAccount);
        paymentAccountRepository.save(receiverAccount);

        return TransactionModel.builder()
                .id(transactionId.toString())
                .amount(transactionModel.getAmount())
                .senderAccountId(transactionModel.getSenderAccountId())
                .receiverAccountId(transactionModel.getReceiverAccountId())
                .status(TransactionModelStatus.SUCCESS.name())
                .comment(transactionModel.getComment())
                .created(Timestamp.from(Instant.now()))
                .modified(Timestamp.from(Instant.now()))
                .build();
    }

    @Transactional
    public TransactionModel rollback(String transactionId) {

        TransactionDataInfo transaction = transactionService.getTransactionById(transactionId);

        PaymentAccount senderAccount = paymentAccountRepository.findById(
                transaction.getSenderAccountId()
        ).orElseThrow(() -> new TransactionValidationException("Invalid sender account id", "senderAccountId", "Invalid account id"));

        PaymentAccount receiverAccount = paymentAccountRepository.findById(
                transaction.getReceiverAccountId()
        ).orElseThrow(() -> new TransactionValidationException("Invalid receiver account id", "receiverAccountId", "Invalid account id"));

        if (senderAccount.getId().equals(receiverAccount.getId())) {
            throw  new TransactionValidationException("Invalid receiver account id", "receiverAccountId", "Invalid account id");
        }

        if (!transaction.getStatus().equals("SUCCESS")) {
            throw  new TransactionException("Cannot rollback unsuccessful transaction");
        }

        if (MoneyConverter.convert(transaction.getAmount()).compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionValidationException("Invalid amount", "amount", "Amount value must be greater than zero");
        }

        if (receiverAccount.getAmount().compareTo(MoneyConverter.convert(transaction.getAmount())) < 0) {
            throw new TransactionValidationException("Not enough money", "amount", "Not enough money");
        }

        senderAccount.setAmount(senderAccount.getAmount().add(MoneyConverter.convert(transaction.getAmount())));
        receiverAccount.setAmount(receiverAccount.getAmount().subtract(MoneyConverter.convert(transaction.getAmount())));

        paymentAccountRepository.save(senderAccount);
        paymentAccountRepository.save(receiverAccount);

        return TransactionModel.builder()
                .id(transactionId)
                .amount(MoneyConverter.convert(transaction.getAmount()))
                .senderAccountId(transaction.getSenderAccountId())
                .receiverAccountId(transaction.getReceiverAccountId())
                .status(TransactionModelStatus.ROLLBACK.name())
                .comment(transaction.getComment())
                .created(Timestamp.from(Instant.now()))
                .modified(Timestamp.from(Instant.now()))
                .build();
    }
}
