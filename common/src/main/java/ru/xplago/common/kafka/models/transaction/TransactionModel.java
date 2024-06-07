package ru.xplago.common.kafka.models.transaction;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionModel {
    private String id;
    private BigDecimal amount;
    private String senderAccountId;
    private String receiverAccountId;
    private String status;
    private String comment;
    private Timestamp created;
    private Timestamp modified;
}
