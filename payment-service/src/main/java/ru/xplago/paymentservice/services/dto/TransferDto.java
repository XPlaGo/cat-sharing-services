package ru.xplago.paymentservice.services.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferDto {
    private Long userId;
    private BigDecimal amount;
    private String senderTransactionId;
    private String receiverTransactionId;
    private String comment;
}
