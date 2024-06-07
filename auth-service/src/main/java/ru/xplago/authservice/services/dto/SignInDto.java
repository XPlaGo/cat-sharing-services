package ru.xplago.authservice.services.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInDto {
    @Email
    private String email;
    @Size(min = 6, max = 256)
    private String password;
}
