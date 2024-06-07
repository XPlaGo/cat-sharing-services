package ru.xplago.tradeservice.converters;

import ru.xplago.common.grpc.cat.CatInfo;
import ru.xplago.common.grpc.trade.SaleOfferFullInfo;
import ru.xplago.common.grpc.trade.SaleOfferInfo;
import ru.xplago.tradeservice.entities.SaleOffer;

public class SaleOfferInfoConverter {
    public static SaleOfferInfo convert(SaleOffer saleOffer) {
        SaleOfferInfo.Builder builder = SaleOfferInfo.newBuilder();
        builder.setId(saleOffer.getId());
        builder.setCatId(saleOffer.getCatId());
        builder.setPrice(MoneyConverter.convert(saleOffer.getPrice()));
        builder.setComment(saleOffer.getComment());
        builder.setSellerOwnerId(saleOffer.getSellerOwnerId());
        builder.setStatus(saleOffer.getStatus().name());
        builder.setCreated(TimestampConverter.convert(saleOffer.getCreated()));
        builder.setModified(TimestampConverter.convert(saleOffer.getModified()));

        return builder.build();
    }

    public static SaleOfferFullInfo convert(SaleOffer saleOffer, CatInfo catInfo) {
        SaleOfferFullInfo.Builder builder = SaleOfferFullInfo.newBuilder();
        builder.setId(saleOffer.getId());
        builder.setCat(CatShortInfoConverter.convert(catInfo));
        builder.setPrice(MoneyConverter.convert(saleOffer.getPrice()));
        builder.setComment(saleOffer.getComment());
        builder.setSellerOwnerId(saleOffer.getSellerOwnerId());
        builder.setStatus(saleOffer.getStatus().name());
        builder.setCreated(TimestampConverter.convert(saleOffer.getCreated()));
        builder.setModified(TimestampConverter.convert(saleOffer.getModified()));

        return builder.build();
    }
}
