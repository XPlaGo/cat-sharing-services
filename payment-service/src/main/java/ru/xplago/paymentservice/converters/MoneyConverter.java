package ru.xplago.paymentservice.converters;

import ru.xplago.common.grpc.payment.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyConverter {
    public static Money convert(BigDecimal amount) {
        BigDecimal integerPart = amount.setScale(0, RoundingMode.DOWN);
        BigDecimal decimalPart = amount.subtract(integerPart);

        int scale = decimalPart.scale();
        BigDecimal decimalPartAsInteger = decimalPart.movePointRight(scale);

        long units = amount.longValue();
        int nanos = decimalPartAsInteger.intValue();

        return Money.newBuilder()
                .setUnits(units)
                .setNanos(nanos)
                .build();
    }

    public static BigDecimal convert(Money money) {
        return new BigDecimal(money.getUnits() + "." + money.getNanos());
    }
}
