package ru.xplago.common.kafka.models.sale.deal;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleDealModel {
    private String id;
    private String saleOfferId;
    private Long buyerOwnerId;
    private String transactionId;
    private SaleDealModelStatus status;
    private String reason;
}
