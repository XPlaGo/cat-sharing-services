package ru.xplago.common.kafka.models.sale.deal;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleDealCatRequestModel {
    private String id;
    private String saleOfferId;
    private Long catId;
    private Long buyerOwnerId;
    private Long sellerOwnerId;
    private SaleDealRequestAction action;
}
