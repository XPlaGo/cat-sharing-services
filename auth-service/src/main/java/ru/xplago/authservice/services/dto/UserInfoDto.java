package ru.xplago.authservice.services.dto;

import lombok.*;
import ru.xplago.authservice.entities.User;

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
    private Timestamp birthdate;
    private String avatar;
    private boolean isBlocked;

    public static UserInfoDto fromUser(User user) {
        return new UserInfoDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getBirthday(),
                user.getAvatar(),
                user.getIsBlocked()
        );
    }
}
