package ru.xplago.common.grpc.security.exceptions;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcAuthExceptionAdvice {
    @GrpcExceptionHandler
    public Status handleVerificationCodeException(UnauthenticatedException exception) {
        return Status.NOT_FOUND.withDescription(exception.getMessage());
    }

    @GrpcExceptionHandler
    public Status handleVerificationCodeException(AuthException exception) {
        return Status.NOT_FOUND.withDescription(exception.getMessage());
    }
}
