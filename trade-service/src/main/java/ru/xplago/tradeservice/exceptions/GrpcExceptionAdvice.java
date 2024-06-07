package ru.xplago.tradeservice.exceptions;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {
    @GrpcExceptionHandler
    public Status handleNotFoundException(NotFoundException exception) {
        return Status.NOT_FOUND.withDescription(exception.getMessage());
    }

    @GrpcExceptionHandler
    public Status handleSaleOfferException(SaleOfferException exception) {
        return Status.INVALID_ARGUMENT.withDescription(exception.getMessage());
    }
}
