package ru.xplago.common.kafka.models.user;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserActionModel {
    private Long userId;
    private UserAction action;
}
