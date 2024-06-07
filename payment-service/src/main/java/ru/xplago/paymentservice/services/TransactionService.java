package ru.xplago.paymentservice.services;

import com.google.protobuf.StringValue;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.common.grpc.transaction.TransactionDataInfo;
import ru.xplago.common.grpc.transaction.TransactionServiceGrpc;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class TransactionService {

    private TransactionServiceGrpc.TransactionServiceBlockingStub stub;

    public TransactionDataInfo getTransactionById(String id) {
        return stub.getTransactionById(StringValue.newBuilder().setValue(id).build());
    }
}
