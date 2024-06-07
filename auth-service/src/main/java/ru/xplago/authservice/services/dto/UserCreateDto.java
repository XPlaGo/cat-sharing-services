package ru.xplago.authservice.services.dto;

import lombok.*;
import ru.xplago.authservice.entities.UserRole;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    private String email;
    private String name;
    private String password;
    private Timestamp birthday;
    private String avatar;
    private List<UserRole> roles;
    private boolean isBlocked;
}
