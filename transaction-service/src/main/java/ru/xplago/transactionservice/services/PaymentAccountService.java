package ru.xplago.transactionservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.common.grpc.payment.PaymentAccountExistsByIdAndUserIdRequest;
import ru.xplago.common.grpc.payment.PaymentAccountServiceGrpc;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentAccountService {
    PaymentAccountServiceGrpc.PaymentAccountServiceBlockingStub stub;

    public boolean paymentAccountExistsByIdAndUserId(final String id, final Long userId) {
        return stub.paymentAccountExistsByIdAndUserId(
                PaymentAccountExistsByIdAndUserIdRequest.newBuilder()
                        .setId(id)
                        .setUserId(userId)
                        .build()

        ).getValue();
    }
}
