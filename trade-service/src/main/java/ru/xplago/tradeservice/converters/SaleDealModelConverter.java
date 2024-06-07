package ru.xplago.tradeservice.converters;

import ru.xplago.common.kafka.models.sale.deal.*;
import ru.xplago.tradeservice.entities.SaleDeal;

public class SaleDealModelConverter {
    public static SaleDealModel toModel(
            SaleDeal saleDeal,
            SaleDealModelStatus saleDealModelStatus
    ) {
        SaleDealModel.SaleDealModelBuilder builder = SaleDealModel.builder()
                .id(saleDeal.getId())
                .saleOfferId(saleDeal.getSaleOffer().getId())
                .buyerOwnerId(saleDeal.getBuyerOwnerId())
                .status(saleDealModelStatus);
        if (saleDeal.getTransactionId() != null) builder.transactionId(saleDeal.getTransactionId());
        return builder.build();
    }

    public static SaleDealPaymentRequestModel toPaymentRequest(
            SaleDeal saleDeal,
            String sellerPaymentAccountId,
            String buyerPaymentAccountId,
            SaleDealRequestAction action
            ) {
        SaleDealPaymentRequestModel.SaleDealPaymentRequestModelBuilder builder = SaleDealPaymentRequestModel.builder()
                .id(saleDeal.getId())
                .price(saleDeal.getSaleOffer().getPrice())
                .sellerPaymentAccountId(sellerPaymentAccountId)
                .buyerPaymentAccountId(buyerPaymentAccountId)
                .action(action);
        if (saleDeal.getTransactionId() != null) builder.transactionId(saleDeal.getTransactionId());
        return builder.build();
    }

    public static SaleDealCatRequestModel toCatRequest(
            SaleDeal saleDeal,
            SaleDealRequestAction action
    ) {
        return SaleDealCatRequestModel.builder()
                .id(saleDeal.getId())
                .saleOfferId(saleDeal.getSaleOffer().getId())
                .catId(saleDeal.getSaleOffer().getCatId())
                .sellerOwnerId(saleDeal.getSaleOffer().getSellerOwnerId())
                .buyerOwnerId(saleDeal.getBuyerOwnerId())
                .action(action)
                .build();
    }
}
