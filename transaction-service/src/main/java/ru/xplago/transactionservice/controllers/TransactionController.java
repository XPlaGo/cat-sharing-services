package ru.xplago.transactionservice.controllers;

import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import ru.xplago.common.grpc.security.annotations.Allow;
import ru.xplago.common.grpc.security.resolvers.UserIdResolver;
import ru.xplago.common.grpc.security.services.GrpcRole;
import ru.xplago.common.grpc.transaction.GetMyTransactionsRequest;
import ru.xplago.common.grpc.transaction.TransactionDataInfo;
import ru.xplago.common.grpc.transaction.TransactionServiceGrpc;
import ru.xplago.transactionservice.converters.TransactionDataInfoConverter;
import ru.xplago.transactionservice.entities.Transaction;
import ru.xplago.transactionservice.exceptions.NotFoundException;
import ru.xplago.transactionservice.services.PaymentAccountService;
import ru.xplago.transactionservice.services.TransactionService;

import java.util.List;

@GrpcService
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionController extends TransactionServiceGrpc.TransactionServiceImplBase {

    private TransactionService transactionService;
    private PaymentAccountService paymentAccountService;

    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    @Override
    public void getMyTransactions(GetMyTransactionsRequest request, StreamObserver<TransactionDataInfo> responseObserver) {
        Long userId = UserIdResolver.resolve();

        if (!paymentAccountService.paymentAccountExistsByIdAndUserId(request.getAccountId(), userId)) {
            throw new NotFoundException("Payment account not found");
        }

        List<Transaction> transactionList = transactionService.findAllByAccountId(request.getAccountId());
        for (Transaction transaction : transactionList) {
            responseObserver.onNext(TransactionDataInfoConverter.convert(transaction));
        }

        responseObserver.onCompleted();
    }

    @Allow(roles = GrpcRole.INTERNAL)
    @Override
    public void getTransactionById(StringValue request, StreamObserver<TransactionDataInfo> responseObserver) {
        Transaction transaction = transactionService.findById(request.getValue());
        responseObserver.onNext(TransactionDataInfoConverter.convert(transaction));
        responseObserver.onCompleted();
    }
}
