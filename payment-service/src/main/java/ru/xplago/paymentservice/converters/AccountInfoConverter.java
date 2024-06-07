package ru.xplago.paymentservice.converters;

import ru.xplago.common.grpc.payment.AccountInfo;
import ru.xplago.paymentservice.entities.PaymentAccount;

public class AccountInfoConverter {
    public static AccountInfo convert(PaymentAccount paymentAccount) {
        return AccountInfo.newBuilder()
                .setId(paymentAccount.getId())
                .setAmount(MoneyConverter.convert(paymentAccount.getAmount()))
                .setUserId(paymentAccount.getUserId())
                .build();
    }
}
