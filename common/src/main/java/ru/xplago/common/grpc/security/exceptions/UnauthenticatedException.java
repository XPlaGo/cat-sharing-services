package ru.xplago.common.grpc.security.exceptions;

public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
