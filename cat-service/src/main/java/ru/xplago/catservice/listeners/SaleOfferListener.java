package ru.xplago.catservice.listeners;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.xplago.catservice.services.CatService;
import ru.xplago.catservice.topics.SaleOfferTopics;
import ru.xplago.common.kafka.models.sale.deal.SaleDealCatRequestModel;
import ru.xplago.common.kafka.models.sale.deal.SaleDealCatResponseModel;
import ru.xplago.common.kafka.models.sale.deal.SaleDealResponseStatus;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class SaleOfferListener {

    private CatService catService;
    private KafkaTemplate<String, SaleDealCatResponseModel> saleDealCatResponseModelKafkaTemplate;

    @KafkaListener(id = "saleOfferCatRequest", topics = "saleOfferCatRequest", groupId = "cat")
    public void listenSaleOffers(SaleDealCatRequestModel saleDealCatRequestModel) {
        switch (saleDealCatRequestModel.getAction()) {
            case NEW -> {
                SaleDealCatResponseModel model;
                try {
                    catService.rebindCat(
                            saleDealCatRequestModel.getCatId(),
                            saleDealCatRequestModel.getSellerOwnerId(),
                            saleDealCatRequestModel.getBuyerOwnerId()
                    );
                    model = SaleDealCatResponseModel.builder()
                            .id(saleDealCatRequestModel.getId())
                            .saleOfferId(saleDealCatRequestModel.getSaleOfferId())
                            .status(SaleDealResponseStatus.ACCEPTED)
                            .build();
                } catch (Exception exception) {
                    log.error(exception.getMessage(), exception);
                    model = SaleDealCatResponseModel.builder()
                            .id(saleDealCatRequestModel.getId())
                            .saleOfferId(saleDealCatRequestModel.getSaleOfferId())
                            .status(SaleDealResponseStatus.REJECTED)
                            .reason(exception.getMessage())
                            .build();
                }
                saleDealCatResponseModelKafkaTemplate.send(SaleOfferTopics.SALE_OFFER_CAT_RESPONSE, model.getId(), model);
            }
            case ROLLBACK -> {
                try {
                    catService.rebindCat(
                            saleDealCatRequestModel.getCatId(),
                            saleDealCatRequestModel.getBuyerOwnerId(),
                            saleDealCatRequestModel.getSellerOwnerId()
                    );
                } catch (Exception exception) {
                    log.error(exception.getMessage(), exception);
                }
            }
        }
    }
}
