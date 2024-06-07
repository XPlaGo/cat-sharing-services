package ru.xplago.tradeservice.converters;

import com.google.protobuf.Timestamp;

import java.time.Instant;

public class TimestampConverter {
    public static Timestamp convert(java.sql.Timestamp timestamp) {
        return Timestamp.newBuilder()
                .setSeconds(timestamp.toInstant().getEpochSecond())
                .setNanos(timestamp.toInstant().getNano())
                .build();
    }

    public static java.sql.Timestamp convert(Timestamp timestamp) {
        return java.sql.Timestamp.from(
                Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos())
        );
    }
}
