package ru.xplago.paymentservice.services.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDto {
    private Long id;
    private String email;
    private String name;
    private Timestamp birthday;
    private String avatar;
    private boolean isBlocked;
}
