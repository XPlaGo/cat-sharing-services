package ru.xplago.tradeservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "sale_offer")
public class SaleOffer extends BaseEntity {
    @Column(nullable = false)
    private Long catId;
    @Column(nullable = false)
    private BigDecimal price;
    private String comment;
    @Column(nullable = false)
    private Long sellerOwnerId;
    @Enumerated(EnumType.STRING)
    private SaleOfferStatus status;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<SaleDeal> deals;
}
