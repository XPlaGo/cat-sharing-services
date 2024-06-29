package ru.xplago.tradeservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.xplago.tradeservice.entities.SaleOffer;
import ru.xplago.tradeservice.entities.SaleOfferStatus;

import java.util.List;
import java.util.Optional;

public interface SaleOfferRepository extends JpaRepository<SaleOffer, String> {
    Optional<SaleOffer> getByCatIdAndSellerOwnerIdAndStatus(Long catId, Long sellerId, SaleOfferStatus status);
    boolean existsByCatId(Long catId);
    boolean existsByCatIdAndStatus(Long catId, SaleOfferStatus status);
    List<SaleOffer> findAllByStatusAndSellerOwnerIdNot(SaleOfferStatus status, Long sellerId);
}
