package ru.xplago.common.kafka.models.transaction;

public enum TransactionModelStatus {
    PENDING,
    SUCCESS,
    REJECTED,
    ROLLBACK
}
