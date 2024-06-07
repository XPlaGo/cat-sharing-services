package ru.xplago.common.grpc.security.interceptors;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.xplago.common.grpc.security.data.GrpcHeader;
import ru.xplago.common.grpc.security.services.JwtService;

public class AuthClientInterceptor implements ClientInterceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JwtService jwtService;

    public AuthClientInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> methodDescriptor,
            CallOptions callOptions,
            Channel channel
    ) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, final Metadata headers) {
                final Listener<RespT> tracingResponseListener = responseListener(responseListener);

                super.start(tracingResponseListener, injectInternalToken(headers));
            }
        };
    }

    private <RespT> ForwardingClientCallListener<RespT> responseListener(final ClientCall.Listener<RespT> responseListener) {
        return new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
            @Override
            public void onClose(Status status, Metadata trailers) {
                handleAuthStatusCode(status);

                super.onClose(status, trailers);
            }
        };
    }

    private Metadata injectInternalToken(Metadata headers) {
        final String authHeader = headers.get(GrpcHeader.AUTHORIZATION);

        if (authHeader == null || authHeader.isEmpty()) {
            final String internalToken = jwtService.getInternal();
            headers.put(GrpcHeader.AUTHORIZATION, internalToken);
        }

        return headers;
    }

    private void handleAuthStatusCode(Status status) {
        if(status.getCode().equals(Status.UNAUTHENTICATED.getCode())) {
            logger.error("Grpc call is unauthenticated.", status.getCause());
        }

        if(status.getCode().equals(Status.PERMISSION_DENIED.getCode())) {
            logger.error("Grpc call is unauthorized.", status.getCause());
        }
    }
}
