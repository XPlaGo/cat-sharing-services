package ru.xplago.common.grpc.validation.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ValidationException extends RuntimeException {
    private Map<String, Map<String, String>> errors;

    public ValidationException(String message, Map<String, Map<String, String>> errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(String message, String field, String fieldMessage) {
        super(message);
        HashMap<String, Map<String, String>> errors = new HashMap<>();
        HashMap<String, String> fieldError = new HashMap<>();
        fieldError.put("message", fieldMessage);
        errors.put(field, fieldError);
        this.errors = errors;
    }
}
