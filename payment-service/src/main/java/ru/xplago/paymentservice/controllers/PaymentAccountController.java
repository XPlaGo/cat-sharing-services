package ru.xplago.paymentservice.controllers;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import ru.xplago.common.grpc.payment.*;
import ru.xplago.common.grpc.security.annotations.Allow;
import ru.xplago.common.grpc.security.resolvers.UserIdResolver;
import ru.xplago.common.grpc.security.services.GrpcRole;
import ru.xplago.paymentservice.converters.AccountInfoConverter;
import ru.xplago.paymentservice.converters.PublicAccountInfoConverter;
import ru.xplago.paymentservice.entities.PaymentAccount;
import ru.xplago.paymentservice.services.PaymentAccountService;
import ru.xplago.paymentservice.services.dto.CreatePaymentAccountDto;
import ru.xplago.paymentservice.services.dto.PublicAccountInfoDto;

import java.util.List;

@GrpcService
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentAccountController extends PaymentAccountServiceGrpc.PaymentAccountServiceImplBase {

    private PaymentAccountService paymentAccountService;

    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    @Override
    public void getMyPaymentAccount(Empty request, StreamObserver<AccountInfo> responseObserver) {
        Long userId = UserIdResolver.resolve();

        PaymentAccount result = paymentAccountService.findByUserId(userId);

        responseObserver.onNext(AccountInfoConverter.convert(result));
        responseObserver.onCompleted();
    }

    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    @Override
    public void getMyPaymentAccounts(Empty request, StreamObserver<AccountsInfo> responseObserver) {
        Long userId = UserIdResolver.resolve();

        List<PaymentAccount> result = paymentAccountService.findAllByUserId(userId);

        responseObserver.onNext(
                AccountsInfo.newBuilder()
                        .addAllAccounts(result.stream().map(AccountInfoConverter::convert).toList())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    @Override
    public void createMyPaymentAccount(Empty request, StreamObserver<AccountInfo> responseObserver) {
        Long userId = UserIdResolver.resolve();

        PaymentAccount result = paymentAccountService.create(
                CreatePaymentAccountDto.builder()
                        .userId(userId)
                        .build()
        );

        responseObserver.onNext(AccountInfoConverter.convert(result));
        responseObserver.onCompleted();
    }

    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    @Override
    public void myAccountExists(Empty request, StreamObserver<BoolValue> responseObserver) {
        Long userId = UserIdResolver.resolve();

        boolean result = paymentAccountService.existsByUserId(userId);

        responseObserver.onNext(
                BoolValue.newBuilder()
                        .setValue(result)
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    @Override
    public void getPaymentAccountByEmail(GetAccountByEmailRequest request, StreamObserver<PublicAccountInfo> responseObserver) {
        PublicAccountInfoDto publicAccountInfoDto = paymentAccountService.getByEmail(request.getEmail());
        responseObserver.onNext(PublicAccountInfoConverter.convert(publicAccountInfoDto));
        responseObserver.onCompleted();
    }

    @Allow(roles = GrpcRole.INTERNAL)
    @Override
    public void paymentAccountExistsByIdAndUserId(PaymentAccountExistsByIdAndUserIdRequest request, StreamObserver<BoolValue> responseObserver) {
        boolean result = paymentAccountService.existsByIdAndUserId(request.getId(), request.getUserId());
        responseObserver.onNext(BoolValue.newBuilder().setValue(result).build());
        responseObserver.onCompleted();
    }

    @Allow(roles = GrpcRole.INTERNAL)
    @Override
    public void getPaymentAccountByUserId(Int64Value request, StreamObserver<AccountInfo> responseObserver) {
        PaymentAccount result = paymentAccountService.findByUserId(request.getValue());
        responseObserver.onNext(AccountInfoConverter.convert(result));
        responseObserver.onCompleted();
    }
}
