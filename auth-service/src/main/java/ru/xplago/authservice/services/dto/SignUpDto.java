package ru.xplago.authservice.services.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {
    private String email;
    private String name;
    private String password;
    private Timestamp birthday;
    private String avatar;
}
