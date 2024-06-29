package ru.xplago.transactionservice.converters;

import com.google.protobuf.Timestamp;
import ru.xplago.common.grpc.transaction.TransactionDataInfo;
import ru.xplago.transactionservice.entities.Transaction;

public class TransactionDataInfoConverter {
    public static TransactionDataInfo convert(Transaction transaction) {
        TransactionDataInfo.Builder builder = TransactionDataInfo.newBuilder()
                .setId(transaction.getId())
                .setAmount(MoneyConverter.convert(transaction.getAmount()))
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
                );
        if (transaction.getSenderAccountId() != null) builder.setSenderAccountId(transaction.getSenderAccountId());
        if (transaction.getReceiverAccountId() != null) builder.setReceiverAccountId(transaction.getReceiverAccountId());
        return builder.build();
    }
}
