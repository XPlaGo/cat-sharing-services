package ru.xplago.authservice.exceptions;

import com.google.gson.Gson;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {
    @GrpcExceptionHandler
    public Status handleVerificationCodeException(VerificationCodeException exception) {
        return Status.INVALID_ARGUMENT.withDescription(exception.getMessage());
    }

    @GrpcExceptionHandler
    public Status handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return Status.ALREADY_EXISTS.withDescription(exception.getMessage());
    }

    @GrpcExceptionHandler
    public Status handleUserNotFoundException(UserNotFoundException exception) {
        return Status.NOT_FOUND.withDescription(exception.getMessage());
    }
}
