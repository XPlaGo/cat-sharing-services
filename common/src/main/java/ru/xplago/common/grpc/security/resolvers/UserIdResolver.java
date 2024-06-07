package ru.xplago.common.grpc.security.resolvers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.xplago.common.grpc.security.exceptions.UnauthenticatedException;

public class UserIdResolver {
    public static Long resolve() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new UnauthenticatedException("Invalid user id passed in access token", e);
        }
    }
}
