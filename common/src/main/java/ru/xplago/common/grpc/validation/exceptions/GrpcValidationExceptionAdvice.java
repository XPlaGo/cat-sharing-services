package ru.xplago.common.grpc.validation.exceptions;

import com.google.gson.Gson;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcValidationExceptionAdvice {
    @GrpcExceptionHandler
    public StatusRuntimeException handleValidationException(ValidationException exception) {
        Status status = Status.INVALID_ARGUMENT.withDescription(exception.getMessage());

        Gson gson = new Gson();
        String serializedErrors = gson.toJson(exception.getErrors());

        Metadata metadata = new Metadata();
        Metadata.Key<String> errorsKey = Metadata.Key.of("errors", Metadata.ASCII_STRING_MARSHALLER);
        metadata.put(errorsKey, serializedErrors);

        return status.asRuntimeException(metadata);
    }
}
