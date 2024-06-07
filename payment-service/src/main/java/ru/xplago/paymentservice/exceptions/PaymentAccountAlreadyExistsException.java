package ru.xplago.paymentservice.exceptions;

public class PaymentAccountAlreadyExistsException extends RuntimeException {
    public PaymentAccountAlreadyExistsException(String message) {
        super(message);
    }
}
