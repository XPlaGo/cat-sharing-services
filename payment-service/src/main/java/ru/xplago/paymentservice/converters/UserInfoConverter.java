package ru.xplago.paymentservice.converters;

import java.sql.Timestamp;
import java.time.Instant;

import ru.xplago.common.grpc.auth.UserInfo;
import ru.xplago.paymentservice.services.dto.UserInfoDto;

public class UserInfoConverter {
    public static UserInfoDto convert(UserInfo userInfo) {
        return UserInfoDto.builder()
                .id(userInfo.getId())
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .birthday(Timestamp.from(
                                Instant.ofEpochSecond(
                                        userInfo.getBirthday().getSeconds(),
                                        userInfo.getBirthday().getNanos()
                                )
                        )
                )
                .avatar(userInfo.getAvatar())
                .isBlocked(userInfo.getIsBlocked())
                .build();
    }

    public static UserInfo convert(UserInfoDto userInfoDto) {
        return UserInfo.newBuilder()
                .setId(userInfoDto.getId())
                .setEmail(userInfoDto.getEmail())
                .setName(userInfoDto.getName())
                .setBirthday(com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(userInfoDto.getBirthday().toInstant().getEpochSecond())
                        .setNanos(userInfoDto.getBirthday().toInstant().getNano())
                        .build())
                .setAvatar(userInfoDto.getAvatar())
                .setIsBlocked(userInfoDto.isBlocked())
                .build();
    }
}
