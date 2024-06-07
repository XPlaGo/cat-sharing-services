package ru.xplago.common.kafka.models.sale.deal;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleDealCatResponseModel {
    private String id;
    private String saleOfferId;
    private SaleDealResponseStatus status;
    private String reason;
}
