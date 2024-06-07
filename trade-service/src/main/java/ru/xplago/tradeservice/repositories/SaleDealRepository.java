package ru.xplago.tradeservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.xplago.tradeservice.entities.SaleDeal;
import ru.xplago.tradeservice.entities.SaleDealStatus;
import ru.xplago.tradeservice.entities.SaleOffer;

import java.util.List;

public interface SaleDealRepository extends JpaRepository<SaleDeal, String> {
    List<SaleDeal> findAllBySaleOffer(SaleOffer saleOffer);
    boolean existsBySaleOfferAndStatus(SaleOffer saleOffer, SaleDealStatus status);
}
