package ru.xplago.tradeservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.xplago.common.grpc.cat.OwnerInfo;
import ru.xplago.common.grpc.payment.AccountInfo;
import ru.xplago.common.kafka.models.sale.deal.*;
import ru.xplago.tradeservice.converters.SaleDealModelConverter;
import ru.xplago.tradeservice.entities.SaleDeal;
import ru.xplago.tradeservice.entities.SaleOffer;
import ru.xplago.tradeservice.entities.SaleOfferStatus;
import ru.xplago.tradeservice.exceptions.NotFoundException;
import ru.xplago.tradeservice.exceptions.SaleOfferException;
import ru.xplago.tradeservice.repositories.SaleOfferRepository;
import ru.xplago.tradeservice.services.dto.CreateSaleOfferDto;
import ru.xplago.tradeservice.topics.SaleDealTopics;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SaleOfferService {
    private SaleOfferRepository saleOfferRepository;
    private OwnerService ownerService;
    private PaymentService paymentService;
    private SaleDealService saleDealService;

    private KafkaTemplate<String, SaleDealPaymentRequestModel> saleDealPaymentRequestModelKafkaTemplate;
    private KafkaTemplate<String, SaleDealCatRequestModel> saleDealCatRequestModelKafkaTemplate;

    public SaleOffer create(CreateSaleOfferDto dto) {
        if (saleOfferRepository.existsByCatIdAndStatus(dto.getCatId(), SaleOfferStatus.OPENED)) {
            throw new SaleOfferException("Sale offer already exists");
        }

        SaleOffer saleOffer = SaleOffer.builder()
                .catId(dto.getCatId())
                .price(dto.getPrice())
                .comment(dto.getComment())
                .sellerOwnerId(dto.getSellerOwnerId())
                .status(SaleOfferStatus.OPENED)
                .created(Timestamp.from(Instant.now()))
                .modified(Timestamp.from(Instant.now()))
                .build();

        return saleOfferRepository.save(saleOffer);
    }

    public SaleOffer save(SaleOffer saleOffer) {
        return saleOfferRepository.save(saleOffer);
    }

    public SaleOffer findById(String id) {
        return saleOfferRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Sale offer not found"));
    }

    public SaleOffer getByCatIdAndOwnerIdAndStatus(Long id, Long ownerId, SaleOfferStatus status) {
        return saleOfferRepository
                .getByCatIdAndSellerOwnerIdAndStatus(id, ownerId, status)
                .orElseThrow(() -> new NotFoundException("Sale offer not found"));
    }

    public List<SaleOffer> getProposedSaleOffersForOwnerId(Long ownerId) {
        return saleOfferRepository.findAllByStatusAndSellerOwnerIdNot(SaleOfferStatus.OPENED, ownerId);
    }

    public SaleDeal buy(String id, Long buyerUserId) {

        SaleOffer saleOffer = findById(id);

        if (saleOffer.getStatus() == SaleOfferStatus.CLOSED || saleDealService.existsConfirmed(saleOffer)) {
            throw new SaleOfferException("Buying is not available");
        }

        OwnerInfo buyerOwnerInfo = ownerService.getOwnerByUserId(buyerUserId);
        AccountInfo buyerPaymentAccountInfo = paymentService.getAccountInfo(buyerUserId);

        OwnerInfo sellerOwnerInfo = ownerService.getOwnerByUserId(saleOffer.getSellerOwnerId());
        Long sellerUserId = sellerOwnerInfo.getUserId();
        AccountInfo sellerPaymentAccountInfo = paymentService.getAccountInfo(sellerUserId);

        SaleDeal saleDeal = saleDealService.init(saleOffer, buyerOwnerInfo.getId());

        saleDealPaymentRequestModelKafkaTemplate.send(SaleDealTopics.SALE_DEAL_PAYMENT_REQUEST, id, SaleDealModelConverter.toPaymentRequest(
                saleDeal,
                sellerPaymentAccountInfo.getId(),
                buyerPaymentAccountInfo.getId(),
                SaleDealRequestAction.NEW
                ));
        saleDealCatRequestModelKafkaTemplate.send(SaleDealTopics.SALE_DEAL_CAT_REQUEST, id, SaleDealModelConverter.toCatRequest(
                saleDeal,
                SaleDealRequestAction.NEW
        ));

        return saleDeal;
    }
}
