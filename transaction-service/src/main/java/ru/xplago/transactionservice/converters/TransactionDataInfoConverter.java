package ru.xplago.transactionservice.converters;

import com.google.protobuf.Timestamp;
import ru.xplago.common.grpc.transaction.TransactionDataInfo;
import ru.xplago.transactionservice.entities.Transaction;

public class TransactionDataInfoConverter {
    public static TransactionDataInfo convert(Transaction transaction) {
        return TransactionDataInfo.newBuilder()
                .setId(transaction.getId())
                .setAmount(MoneyConverter.convert(transaction.getAmount()))
                .setSenderAccountId(transaction.getSenderAccountId())
                .setReceiverAccountId(transaction.getReceiverAccountId())
                .setComment(transaction.getComment())
                .setStatus(transaction.getStatus().name())
                .setCreated(Timestamp.newBuilder()
                        .setSeconds(transaction.getCreated().toInstant().getEpochSecond())
                        .setNanos(transaction.getCreated().toInstant().getNano())
                        .build()
                )
                .setModified(Timestamp.newBuilder()
                        .setSeconds(transaction.getModified().toInstant().getEpochSecond())
                        .setNanos(transaction.getModified().toInstant().getNano())
                        .build()
                )
                .build();
    }
}
