package ru.xplago.catservice.controllers;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.xplago.catservice.entities.Owner;
import ru.xplago.catservice.services.CatService;
import ru.xplago.catservice.services.OwnerService;
import ru.xplago.catservice.services.dto.CreateOwnerDto;
import ru.xplago.common.grpc.cat.OwnerInfo;
import ru.xplago.common.grpc.cat.OwnerServiceGrpc;
import ru.xplago.common.grpc.security.annotations.Allow;
import ru.xplago.common.grpc.security.exceptions.UnauthenticatedException;
import ru.xplago.common.grpc.security.resolvers.UserIdResolver;
import ru.xplago.common.grpc.security.services.GrpcRole;

@GrpcService
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OwnerController extends OwnerServiceGrpc.OwnerServiceImplBase {

    private OwnerService ownerService;
    private CatService catService;

    @Override
    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    public void getMyOwner(Empty request, StreamObserver<OwnerInfo> responseObserver) {
        Long userId = UserIdResolver.resolve();

        Owner owner = ownerService.findByUserId(userId);
        OwnerInfo ownerInfo = toOwnerInfo(owner);

        responseObserver.onNext(ownerInfo);
        responseObserver.onCompleted();
    }

    @Override
    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    public void createMyOwner(Empty request, StreamObserver<OwnerInfo> responseObserver) {
        Long userId = UserIdResolver.resolve();

        Owner owner = ownerService.createOwner(CreateOwnerDto.builder()
                .userId(userId)
                .build()
        );
        OwnerInfo ownerInfo = toOwnerInfo(owner);

        responseObserver.onNext(ownerInfo);
        responseObserver.onCompleted();
    }

    @Override
    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    public void myOwnerExists(Empty request, StreamObserver<BoolValue> responseObserver) {
        Long userId = UserIdResolver.resolve();

        boolean result = ownerService.existsByUserId(userId);

        responseObserver.onNext(BoolValue.of(result));
        responseObserver.onCompleted();
    }

    @Override
    @Allow(roles = GrpcRole.INTERNAL)
    public void getOwnerByUserId(Int64Value request, StreamObserver<OwnerInfo> responseObserver) {
        Owner owner = ownerService.findByUserId(request.getValue());
        OwnerInfo ownerInfo = toOwnerInfo(owner);

        responseObserver.onNext(ownerInfo);
        responseObserver.onCompleted();
    }

    @Override
    @Allow(roles = GrpcRole.INTERNAL)
    public void getOwnerById(Int64Value request, StreamObserver<OwnerInfo> responseObserver) {
        Owner owner = ownerService.findById(request.getValue());
        OwnerInfo ownerInfo = toOwnerInfo(owner);

        responseObserver.onNext(ownerInfo);
        responseObserver.onCompleted();
    }

    private OwnerInfo toOwnerInfo(Owner owner) {
        return OwnerInfo.newBuilder()
                .setId(owner.getId())
                .setUserId(owner.getUserId())
                .addAllCatsIds(catService.findCatsIdsByOwner(owner))
                .addAllRentedCatsIds(catService.findRentedCatsIdsByTenant(owner))
                .build();
    }
}
