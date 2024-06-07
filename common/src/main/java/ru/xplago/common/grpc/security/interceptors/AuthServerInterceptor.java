package ru.xplago.common.grpc.security.interceptors;

import com.google.common.collect.Sets;
import io.grpc.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.xplago.common.grpc.security.annotations.Allow;
import ru.xplago.common.grpc.security.data.AllowedMethod;
import ru.xplago.common.grpc.security.data.GrpcHeader;
import ru.xplago.common.grpc.security.data.GrpcJwtContext;
import ru.xplago.common.grpc.security.data.JwtContextData;
import ru.xplago.common.grpc.security.exceptions.AuthException;
import ru.xplago.common.grpc.security.exceptions.UnauthenticatedException;
import ru.xplago.common.grpc.security.services.JwtService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@GrpcGlobalServerInterceptor
public class AuthServerInterceptor implements ServerInterceptor {

    private static final String GRPC_FIELD_MODIFIER = "_";
    private static final String BEARER = "Bearer";
    private static final ServerCall.Listener NOOP_LISTENER = new ServerCall.Listener    () {};
    private static final Logger log = LoggerFactory.getLogger(AuthServerInterceptor.class);

    private final AllowedCollector allowedCollector;
    private final JwtService jwtService;
    private final Environment environment;

    public AuthServerInterceptor(
            AllowedCollector allowedCollector,
            JwtService jwtService,
            Environment environment
    ) {
        this.allowedCollector = allowedCollector;
        this.jwtService = jwtService;
        this.environment = environment;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {
        try {
            final JwtContextData contextData = parseAuthContextData(metadata);
            final Context context = Context.current().withValue(GrpcJwtContext.CONTEXT_DATA_KEY, contextData);

            return buildListener(serverCall, metadata, serverCallHandler, context, contextData);
        } catch (UnauthenticatedException e) {
            serverCall.close(Status.UNAUTHENTICATED.withDescription(e.getMessage()).withCause(e.getCause()), metadata);
            return NOOP_LISTENER;
        }
    }

    private <ReqT, RespT> ForwardingServerCallListener<ReqT> buildListener(
            ServerCall<ReqT, RespT> call,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> next,
            Context context,
            JwtContextData contextData
    ) {
        final ServerCall.Listener<ReqT> customDelegate = Contexts.interceptCall(context, call, metadata, next);

        return new ForwardingServerCallListener<ReqT>() {
            @SuppressWarnings("uncheked")
            ServerCall.Listener<ReqT> delegate = NOOP_LISTENER;

            @Override
            protected ServerCall.Listener<ReqT> delegate() {
                return delegate;
            }

            @Override
            public void onMessage(ReqT request) {
                try {
                    if (delegate == NOOP_LISTENER) {
                        final String methodName = call.getMethodDescriptor().getFullMethodName().toLowerCase();

                        validateAnnotatedMethods(request, contextData, methodName);

                        if (contextData != null) {
                            SecurityContextHolder.getContext().setAuthentication(
                                    new UsernamePasswordAuthenticationToken(contextData.getUserId(), contextData.getRoles())
                            );
                        }
                        delegate = customDelegate;
                    }
                } catch (AuthException e) {
                    call.close(Status.PERMISSION_DENIED.withDescription(e.getMessage()).withCause(e.getCause()), metadata);
                }
                super.onMessage(request);
            }
        };
    }

    private <ReqT> void validateAnnotatedMethods(ReqT request, JwtContextData contextData, String methodName) {
        if(!validateExposedAnnotation(contextData, methodName)) {
            validateAllowedAnnotation(request, contextData, methodName);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean validateExposedAnnotation(JwtContextData contextData, String methodName) {
        final Set<String> exposedToEnvironments = allowedCollector.getExposedEnv(methodName).orElse(Sets.newHashSet());
        final boolean methodIsExposed = Arrays.stream(environment.getActiveProfiles())
                .anyMatch(exposedToEnvironments::contains);

        if(methodIsExposed) {
            if(contextData == null) throw new AuthException("Missing JWT data.");

            final List<String> rawEnvironments = (List<String>) contextData
                    .getJwtClaims().get(JwtService.TOKEN_ENV, List.class);
            final Set<String> environments = rawEnvironments.stream().map(Object::toString).collect(Collectors.toSet());

            return exposedToEnvironments.stream().anyMatch(environments::contains);
        }

        return false;
    }

    private <ReqT> void validateAllowedAnnotation(ReqT request, JwtContextData contextData, String methodName) {
        Optional<AllowedMethod> opt = allowedCollector.getAllowedAuth(methodName);
                opt.ifPresent(value -> authorizeOwnerOrRoles(request, contextData, value));
    }

    private <ReqT> void authorizeOwnerOrRoles(ReqT request, JwtContextData contextData, AllowedMethod allowedMethod) {
        if(contextData == null) throw new AuthException("Missing JWT data.");
        if(allowedMethod.getOwnerField().isEmpty()) {
            validateRoles(new HashSet<>(allowedMethod.getRoles()), contextData.getRoles());
        } else {
            authorizeOwner(request, contextData, allowedMethod);
        }
    }

    private <ReqT> String parseOwner(ReqT request, String fieldName) {
        try {
            final Field field = request.getClass().getDeclaredField(fieldName + GRPC_FIELD_MODIFIER);
            field.setAccessible(true);
            return String.valueOf(field.get(request));
        } catch(NoSuchFieldException | IllegalAccessException e) {
            throw new AuthException("Missing owner field.");
        }
    }

    private <ReqT> void authorizeOwner(ReqT request, JwtContextData jwtContext, AllowedMethod allowedMethod) {
        final String uid = parseOwner(request, allowedMethod.getOwnerField());

        if(!jwtContext.getUserId().equals(uid)) validateRoles(new HashSet<>(allowedMethod.getRoles()), jwtContext.getRoles());
    }

    private void validateRoles(Set<String> requiredRoles, Set<String> userRoles) {

        if(requiredRoles.isEmpty()) {
            throw new AuthException("Endpoint does not have specified roles.");
        }

        requiredRoles.retainAll(Objects.requireNonNull(userRoles));

        if(requiredRoles.isEmpty()) {
            throw new AuthException("Missing required permission roles.");
        }
    }

    @SuppressWarnings("unchecked")
    private JwtContextData parseAuthContextData(Metadata metadata) {
        try {
            final String authHeaderData = metadata.get(GrpcHeader.AUTHORIZATION);

            if(authHeaderData == null) {
                return null;
            }

            final String token = authHeaderData.replace(BEARER, "").trim();
            final Claims jwtBody = Jwts.parser().setSigningKey(jwtService.getKey()).parseClaimsJws(token).getBody();
            final List<String> roles = (List<String>) jwtBody.get(JwtService.JWT_ROLES, List.class);

            return new JwtContextData(token, jwtBody.getSubject(), Sets.newHashSet(roles), jwtBody);
        } catch(JwtException | IllegalArgumentException e) {
            throw new UnauthenticatedException(e.getMessage(), e);
        }
    }
}
