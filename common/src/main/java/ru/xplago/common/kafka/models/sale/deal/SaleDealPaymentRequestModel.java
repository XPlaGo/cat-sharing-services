package ru.xplago.common.kafka.models.sale.deal;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleDealPaymentRequestModel {
    private String id;
    private String saleOfferId;
    private BigDecimal price;
    private String buyerPaymentAccountId;
    private String sellerPaymentAccountId;
    private String transactionId;
    private SaleDealRequestAction action;
}
