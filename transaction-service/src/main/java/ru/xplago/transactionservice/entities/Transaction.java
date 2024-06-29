package ru.xplago.transactionservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Transaction {
    @Id
    private String id;

    @Column(nullable = false)
    private BigDecimal amount;

    private String senderAccountId;

    private String receiverAccountId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TransactionStatus status;

    private String comment;

    private Timestamp created;
    private Timestamp modified;
}
