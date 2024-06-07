package ru.xplago.common.grpc.validation.validators;

import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;
import ru.xplago.common.grpc.validation.exceptions.ValidationException;

import java.util.HashMap;
import java.util.Map;

@Service
public class ModelValidator {
    private final SmartValidator smartValidator;

    private ModelValidator(SmartValidator smartValidator) {
        this.smartValidator = smartValidator;
    }

    public  <T> void validate(T data) {
        Map<String, Map<String, String>> validationResult = getMapOfErrors(data);
        if (!validationResult.isEmpty()) {
            throw new ValidationException("Validation failed", validationResult);
        }
    }

    private <T> Map<String, Map<String, String>> getMapOfErrors(T data) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(data, data.getClass().getName());
        smartValidator.validate(data, bindingResult);

        return appendErrorsToMap(bindingResult);
    }

    private Map<String, Map<String, String>> appendErrorsToMap(BeanPropertyBindingResult bindingResult) {
        Map<String, Map<String, String>> errorMap = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            Map<String, String> errorDetails = new HashMap<>();
            if (error.getRejectedValue() != null) errorDetails.put("rejectedValue", error.getRejectedValue().toString());
            errorDetails.put("message", error.getDefaultMessage());
            errorMap.put(error.getField(), errorDetails);
        }
        return errorMap;
    }
}
