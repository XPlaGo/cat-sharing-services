package ru.xplago.transactionservice.exceptions;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {
    @GrpcExceptionHandler
    public Status handleNotFoundException(NotFoundException exception) {
        return Status.NOT_FOUND.withDescription(exception.getMessage());
    }
}
