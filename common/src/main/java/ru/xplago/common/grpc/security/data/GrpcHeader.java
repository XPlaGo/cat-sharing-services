package ru.xplago.common.grpc.security.data;

import io.grpc.Metadata;

public class GrpcHeader {
    private GrpcHeader() {}

    private static final String AUTHORIZATION_KEY = "Authorization";

    public static  final Metadata.Key<String> AUTHORIZATION
            = Metadata.Key.of(AUTHORIZATION_KEY, Metadata.ASCII_STRING_MARSHALLER);
}
