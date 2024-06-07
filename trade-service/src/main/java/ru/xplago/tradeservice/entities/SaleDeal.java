package ru.xplago.tradeservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "sale_deal")
public class SaleDeal extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    private SaleOffer saleOffer;
    private Long buyerOwnerId;
    private String transactionId;
    @Enumerated(EnumType.STRING)
    private SaleDealStatus status;
}
