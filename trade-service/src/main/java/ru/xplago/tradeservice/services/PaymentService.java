package ru.xplago.tradeservice.services;

import com.google.protobuf.Int64Value;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.common.grpc.payment.AccountInfo;
import ru.xplago.common.grpc.payment.PaymentAccountServiceGrpc;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentService {
    PaymentAccountServiceGrpc.PaymentAccountServiceBlockingStub stub;

    public AccountInfo getAccountInfo(Long userId) {
        return stub.getPaymentAccountByUserId(Int64Value.newBuilder().setValue(userId).build());
    }
}
