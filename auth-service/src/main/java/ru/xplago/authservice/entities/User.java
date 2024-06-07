package ru.xplago.authservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "auth_user")
public class User extends BaseEntity {
    private String email;
    private String name;
    private Timestamp birthday;
    @Nullable
    private String avatar;
    private String password;
    private String salt;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<UserRole> roles;
    private Boolean isBlocked;
}