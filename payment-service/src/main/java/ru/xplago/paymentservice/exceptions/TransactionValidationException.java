package ru.xplago.paymentservice.exceptions;

import ru.xplago.common.grpc.validation.exceptions.ValidationException;

import java.util.Map;

public class TransactionValidationException extends ValidationException {
    public TransactionValidationException(String message, Map<String, Map<String, String>> errors) {
        super(message, errors);
    }

    public TransactionValidationException(String message, String field, String fieldMessage) {
        super(message, field, fieldMessage);
    }
}
