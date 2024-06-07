package ru.xplago.paymentservice.services.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PublicAccountInfoDto {
    private String id;
    private UserInfoDto user;
}
