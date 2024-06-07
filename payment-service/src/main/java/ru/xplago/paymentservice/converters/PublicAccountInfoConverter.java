package ru.xplago.paymentservice.converters;

import ru.xplago.common.grpc.payment.PublicAccountInfo;
import ru.xplago.paymentservice.services.dto.PublicAccountInfoDto;

public class PublicAccountInfoConverter {
    public static PublicAccountInfo convert(final PublicAccountInfoDto publicAccountInfoDto) {
        return PublicAccountInfo.newBuilder()
                .setId(publicAccountInfoDto.getId())
                .setUser(UserInfoConverter.convert(publicAccountInfoDto.getUser()))
                .build();
    }
}
