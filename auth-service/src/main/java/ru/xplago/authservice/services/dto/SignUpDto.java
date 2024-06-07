package ru.xplago.authservice.services.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {
    @NotBlank(message = "Name is required")
    @Email(message = "Email format")
    private String email;
    @NotBlank(message = "Name is required")
    @Size(min = 4, max = 20, message = "Name length must be between 4 and 20")
    private String name;
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 256, message = "Password length must be between 6 and 256")
    private String password;
    @NotNull(message = "Birthday is required")
    private Timestamp birthday;
    @URL(message = "Url format")
    private String avatar;
}
