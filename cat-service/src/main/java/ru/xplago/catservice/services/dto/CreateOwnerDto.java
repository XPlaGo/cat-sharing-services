package ru.xplago.catservice.services.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CreateOwnerDto {
    private Long userId;
}
