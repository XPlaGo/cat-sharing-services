package ru.xplago.catservice.services.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.xplago.catservice.entities.Owner;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateCatDto {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 20)
    private String name;
    @NotNull(message = "Birthday is required")
    private Timestamp birthday;
    private Owner owner;
    private Owner tenant;
    @NotNull(message = "Avatar is required")
    private String avatarId;
}
