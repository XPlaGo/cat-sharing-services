package ru.xplago.tradeservice.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.xplago.tradeservice.entities.SaleDeal;
import ru.xplago.tradeservice.entities.SaleDealStatus;
import ru.xplago.tradeservice.entities.SaleOffer;
import ru.xplago.tradeservice.exceptions.NotFoundException;
import ru.xplago.tradeservice.repositories.SaleDealRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class SaleDealService {
    private SaleDealRepository saleDealRepository;

    public SaleDeal save(SaleDeal saleDeal) {
        return saleDealRepository.save(saleDeal);
    }

    public SaleDeal findById(String id) {
        return saleDealRepository.findById(id).orElseThrow(() -> new NotFoundException("Sale deal not found"));
    }

    public List<SaleDeal> findAllBySaleOffer(SaleOffer saleOffer) {
        return saleDealRepository.findAllBySaleOffer(saleOffer);
    }

    public boolean existsConfirmed(SaleOffer saleOffer) {
        return saleDealRepository.existsBySaleOfferAndStatus(saleOffer, SaleDealStatus.CONFIRMED);
    }

    public SaleDeal init(SaleOffer saleOffer, Long buyerOwnerId) {
        SaleDeal.SaleDealBuilder<?, ?> saleDealBuilder = SaleDeal.builder();
        saleDealBuilder.saleOffer(saleOffer);
        saleDealBuilder.buyerOwnerId(buyerOwnerId);
        saleDealBuilder.status(SaleDealStatus.PENDING);
        saleDealBuilder.created(Timestamp.from(Instant.now()));
        saleDealBuilder.modified(Timestamp.from(Instant.now()));
        return save(saleDealBuilder.build());
    }
}
