package ru.xplago.common.grpc.security.data;

import lombok.Getter;

import java.util.Set;

@Getter
public class AllowedMethod {
    private final String method;
    private final String ownerField;
    private final Set<String> roles;

    public AllowedMethod(String method, String ownerField, Set<String> roles) {
        this.method = method;
        this.ownerField = ownerField;
        this.roles = roles;
    }
}
