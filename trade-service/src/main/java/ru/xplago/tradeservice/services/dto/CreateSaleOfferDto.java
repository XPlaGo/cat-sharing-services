package ru.xplago.tradeservice.services.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSaleOfferDto {
    private Long catId;
    private BigDecimal price;
    private String comment;
    private Long sellerOwnerId;
    private Timestamp created;
    private Timestamp modified;
}
