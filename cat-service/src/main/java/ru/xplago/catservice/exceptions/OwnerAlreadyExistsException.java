package ru.xplago.catservice.exceptions;

public class OwnerAlreadyExistsException extends RuntimeException {
    public OwnerAlreadyExistsException(String message) {
        super(message);
    }
}
