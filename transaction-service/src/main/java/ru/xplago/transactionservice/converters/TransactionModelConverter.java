package ru.xplago.transactionservice.converters;

import ru.xplago.transactionservice.entities.Transaction;
import ru.xplago.transactionservice.entities.TransactionStatus;
import ru.xplago.common.kafka.models.transaction.TransactionModel;

public class TransactionModelConverter {
    public static Transaction convert(TransactionModel transactionModel) {
        return Transaction.builder()
                .id(transactionModel.getId())
                .amount(transactionModel.getAmount())
                .senderAccountId(transactionModel.getSenderAccountId())
                .receiverAccountId(transactionModel.getReceiverAccountId())
                .status(TransactionStatus.valueOf(transactionModel.getStatus()))
                .comment(transactionModel.getComment())
                .created(transactionModel.getCreated())
                .modified(transactionModel.getModified())
                .build();
    }
}
