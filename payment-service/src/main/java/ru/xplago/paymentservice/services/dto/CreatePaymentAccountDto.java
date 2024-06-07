package ru.xplago.paymentservice.services.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePaymentAccountDto {
    private Long userId;
}
