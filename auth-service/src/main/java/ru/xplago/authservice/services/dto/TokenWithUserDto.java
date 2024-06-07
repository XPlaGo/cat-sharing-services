package ru.xplago.authservice.services.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenWithUserDto {
    String accessToken;
    UserInfoDto userInfo;
}
