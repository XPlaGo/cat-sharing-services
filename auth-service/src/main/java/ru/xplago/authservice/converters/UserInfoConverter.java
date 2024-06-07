package ru.xplago.authservice.converters;

import com.google.protobuf.Timestamp;
import ru.xplago.authservice.entities.User;
import ru.xplago.authservice.services.dto.UserInfoDto;
import ru.xplago.common.grpc.auth.UserInfo;

public class UserInfoConverter {
    public static UserInfo convert(UserInfoDto dto) {
        return UserInfo.newBuilder()
                .setId(dto.getId())
                .setEmail(dto.getEmail())
                .setName(dto.getName())
                .setBirthday(
                        Timestamp.newBuilder()
                                .setSeconds(dto.getBirthday().toInstant().getEpochSecond())
                                .setNanos(dto.getBirthday().toInstant().getNano())
                                .build())
                .setAvatar(dto.getAvatar())
                .setIsBlocked(dto.isBlocked())
                .build();
    }

    public static UserInfo convert(User user) {
        return UserInfo.newBuilder()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setName(user.getName())
                .setBirthday(
                        Timestamp.newBuilder()
                                .setSeconds(user.getBirthday().toInstant().getEpochSecond())
                                .setNanos(user.getBirthday().toInstant().getNano())
                                .build())
                .setAvatar(user.getAvatar())
                .setIsBlocked(user.getIsBlocked())
                .build();
    }
}
