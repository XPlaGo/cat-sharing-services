package ru.xplago.authservice.exceptions;

import ru.xplago.common.grpc.validation.exceptions.ValidationException;

import java.util.Map;

public class InvalidPasswordException extends ValidationException {
    public InvalidPasswordException(String message, Map<String, Map<String, String>> errors) {
        super(message, errors);
    }
}