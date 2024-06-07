package ru.xplago.authservice.services.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInDto {
    private String email;
    private String password;
}
