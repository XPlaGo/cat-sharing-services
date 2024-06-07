package ru.xplago.tradeservice.controllers;

import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import ru.xplago.common.grpc.cat.CatInfo;
import ru.xplago.common.grpc.cat.OwnerInfo;
import ru.xplago.common.grpc.security.annotations.Allow;
import ru.xplago.common.grpc.security.resolvers.UserIdResolver;
import ru.xplago.common.grpc.trade.*;
import ru.xplago.tradeservice.converters.MoneyConverter;
import ru.xplago.tradeservice.converters.SaleOfferInfoConverter;
import ru.xplago.tradeservice.entities.SaleDeal;
import ru.xplago.tradeservice.entities.SaleOffer;
import ru.xplago.tradeservice.entities.SaleOfferStatus;
import ru.xplago.tradeservice.exceptions.NotFoundException;
import ru.xplago.tradeservice.listeners.SaleDealListener;
import ru.xplago.tradeservice.services.CatService;
import ru.xplago.tradeservice.services.OwnerService;
import ru.xplago.tradeservice.services.SaleOfferService;
import ru.xplago.tradeservice.services.dto.CreateSaleOfferDto;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@GrpcService
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SaleOfferController extends SaleOfferServiceGrpc.SaleOfferServiceImplBase {

    private final CatService catService;
    private OwnerService ownerService;
    private SaleOfferService saleOfferService;
    private SaleDealListener saleDealListener;

    @Override
    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    public void createSaleOffer(CreateSaleOfferRequest request, StreamObserver<SaleOfferInfo> responseObserver) {
        Long userId = UserIdResolver.resolve();

        OwnerInfo ownerInfo = ownerService.getOwnerByUserId(userId);

        SaleOffer saleOffer = saleOfferService.create(CreateSaleOfferDto.builder()
                        .catId(request.getCatId())
                        .price(MoneyConverter.convert(request.getPrice()))
                        .comment(request.getComment())
                        .sellerOwnerId(ownerInfo.getId())
                        .created(Timestamp.from(Instant.now()))
                        .modified(Timestamp.from(Instant.now()))
                .build());

        responseObserver.onNext(SaleOfferInfoConverter.convert(saleOffer));
        responseObserver.onCompleted();
    }

    @Override
    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    public void getSaleOfferById(StringValue request, StreamObserver<SaleOfferInfo> responseObserver) {
        SaleOffer saleOffer = saleOfferService.findById(request.getValue());

        if (saleOffer.getStatus() == SaleOfferStatus.CLOSED) {
            Long userId = UserIdResolver.resolve();

            OwnerInfo ownerInfo = ownerService.getOwnerByUserId(userId);

            if (saleOffer.getSellerOwnerId() != ownerInfo.getId()) {
                throw new NotFoundException("Sale offer not found");
            }
        }

        responseObserver.onNext(SaleOfferInfoConverter.convert(saleOffer));
        responseObserver.onCompleted();
    }

    @Override
    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    public void getSaleFullOfferById(StringValue request, StreamObserver<SaleOfferFullInfo> responseObserver) {
        SaleOffer saleOffer = saleOfferService.findById(request.getValue());

        if (saleOffer.getStatus() == SaleOfferStatus.CLOSED) {
            Long userId = UserIdResolver.resolve();

            OwnerInfo ownerInfo = ownerService.getOwnerByUserId(userId);

            if (saleOffer.getSellerOwnerId() != ownerInfo.getId()) {
                throw new NotFoundException("Sale offer not found");
            }
        }

        CatInfo catInfo = catService.getCatInfo(saleOffer.getCatId());

        responseObserver.onNext(SaleOfferInfoConverter.convert(saleOffer, catInfo));
        responseObserver.onCompleted();
    }

    @Override
    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    public void getSaleOfferByCatId(Int64Value request, StreamObserver<SaleOfferInfo> responseObserver) {
        Long userId = UserIdResolver.resolve();

        OwnerInfo ownerInfo = ownerService.getOwnerByUserId(userId);
        SaleOffer saleOffer = saleOfferService.getByCatIdAndOwnerId(request.getValue(), ownerInfo.getId());

        responseObserver.onNext(SaleOfferInfoConverter.convert(saleOffer));
        responseObserver.onCompleted();
    }

    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    @Override
    public void getSaleOffersIds(Empty request, StreamObserver<StringValue> responseObserver) {
        Long userId = UserIdResolver.resolve();

        OwnerInfo ownerInfo = ownerService.getOwnerByUserId(userId);

        List<SaleOffer> offers = saleOfferService.getProposedSaleOffersForOwnerId(ownerInfo.getId());

        for (SaleOffer offer : offers) {
            responseObserver.onNext(StringValue.newBuilder().setValue(offer.getId()).build());
        }

        responseObserver.onCompleted();
    }

    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    @Override
    public void buy(BuyRequest request, StreamObserver<BuyTransactionInfo> responseObserver) {
        Long userId = UserIdResolver.resolve();

        responseObserver.onNext(
                BuyTransactionInfo.newBuilder()
                        .setStatus("PENDING")
                        .build()
        );

        try {
            SaleDeal saleDeal = saleOfferService.buy(request.getSaleOfferId(), userId);

            if (saleDealListener.observerExist(saleDeal.getId())) {
                responseObserver.onNext(BuyTransactionInfo.newBuilder()
                        .setStatus("REJECTED")
                        .setReason("The deal is already being processed")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            saleDealListener.addObserver(saleDeal.getId(), responseObserver);
        } catch (Exception exception) {
            responseObserver.onNext(BuyTransactionInfo.newBuilder()
                    .setStatus("REJECTED")
                    .setReason(exception.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }
}
