package ru.xplago.catservice.exceptions;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {
    @GrpcExceptionHandler
    public Status handleVerificationCodeException(NotFoundException exception) {
        return Status.NOT_FOUND.withDescription(exception.getMessage());
    }

    @GrpcExceptionHandler
    public Status handleOwnerAlreadyExistsException(OwnerAlreadyExistsException exception) {
        return Status.ALREADY_EXISTS.withDescription(exception.getMessage());
    }
}
