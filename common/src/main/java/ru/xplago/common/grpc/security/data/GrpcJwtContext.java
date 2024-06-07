package ru.xplago.common.grpc.security.data;

import io.grpc.Context;

import java.util.Optional;

public class GrpcJwtContext {
    private GrpcJwtContext() {}

    private static final String CONTEXT_DATA = "context_data";

    public static final Context.Key<JwtContextData> CONTEXT_DATA_KEY = Context.key(CONTEXT_DATA);

    public static Optional<JwtContextData> get() {
        return Optional.ofNullable(CONTEXT_DATA_KEY.get());
    }
}
