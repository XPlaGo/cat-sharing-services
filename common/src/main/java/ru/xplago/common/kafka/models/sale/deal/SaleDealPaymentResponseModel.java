package ru.xplago.common.kafka.models.sale.deal;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleDealPaymentResponseModel {
    private String id;
    private String saleOfferId;
    private String transactionId;
    private String buyerPaymentAccountId;
    private String sellerPaymentAccountId;
    private SaleDealResponseStatus status;
    private String reason;
}
