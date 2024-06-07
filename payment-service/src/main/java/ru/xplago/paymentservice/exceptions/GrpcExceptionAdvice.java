package ru.xplago.paymentservice.exceptions;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {
    @GrpcExceptionHandler
    public Status handleVerificationCodeException(NotFoundException exception) {
        return Status.NOT_FOUND.withDescription(exception.getMessage());
    }

    @GrpcExceptionHandler
    public Status handleOwnerAlreadyExistsException(PaymentAccountAlreadyExistsException exception) {
        return Status.ALREADY_EXISTS.withDescription(exception.getMessage());
    }

    @GrpcExceptionHandler
    public Status handleTransactionException(TransactionException transactionException) {
        return Status.INVALID_ARGUMENT.withDescription(transactionException.getMessage());
    }

    @GrpcExceptionHandler
    public Status handleStatusRuntimeException(StatusRuntimeException exception) {
        return exception.getStatus().withDescription(exception.getMessage());
    }
}
