package ru.xplago.catservice.controllers;

import com.google.protobuf.Int64Value;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.xplago.catservice.entities.Cat;
import ru.xplago.catservice.entities.Owner;
import ru.xplago.catservice.services.CatService;
import ru.xplago.catservice.services.OwnerService;
import ru.xplago.catservice.services.dto.CreateCatDto;
import ru.xplago.common.grpc.cat.CatInfo;
import ru.xplago.common.grpc.cat.CatServiceGrpc;
import ru.xplago.common.grpc.cat.CreateCatRequest;
import ru.xplago.common.grpc.cat.GetCatRequest;
import ru.xplago.common.grpc.security.annotations.Allow;
import ru.xplago.common.grpc.security.exceptions.UnauthenticatedException;
import ru.xplago.common.grpc.security.services.GrpcRole;

import java.time.Instant;

@GrpcService
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class CatController extends CatServiceGrpc.CatServiceImplBase {
    private CatService catService;
    private OwnerService ownerService;

    @Override
    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    public void getCat(GetCatRequest request, StreamObserver<CatInfo> responseObserver) {
        Long userId = getUserId();

        Owner owner = ownerService.findByUserId(userId);
        Cat cat = catService.findCatByIdAndOwner(request.getId(), owner);
        CatInfo catInfo = toCatInfo(cat);

        responseObserver.onNext(catInfo);
        responseObserver.onCompleted();
    }

    @Override
    @Allow(roles = {GrpcRole.INTERNAL})
    public void getCatById(Int64Value request, StreamObserver<CatInfo> responseObserver) {
        Cat cat = catService.findCatById(request.getValue());
        CatInfo catInfo = toCatInfo(cat);

        responseObserver.onNext(catInfo);
        responseObserver.onCompleted();
    }

    @Override
    @Allow(roles = {"ROLE_USER", "ROLE_ADMIN"})
    public void createCat(CreateCatRequest request, StreamObserver<CatInfo> responseObserver) {
        Long userId = getUserId();

        Owner owner = ownerService.findByUserId(userId);
        Cat cat = catService.createCat(CreateCatDto.builder()
                .name(request.getName())
                .birthday(request.getBirthday().getSeconds() == 0 ? null :
                        java.sql.Timestamp.from(
                                Instant.ofEpochSecond(
                                        request.getBirthday().getSeconds(),
                                        request.getBirthday().getNanos())))
                .owner(owner)
                .tenant(null)
                .avatarId(request.getAvatarId())
                .build()
        );
        CatInfo catInfo = toCatInfo(cat);

        responseObserver.onNext(catInfo);
        responseObserver.onCompleted();
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new UnauthenticatedException("Invalid user id passed in access token", e);
        }
    }

    private CatInfo toCatInfo(Cat cat) {

        Timestamp birthday = Timestamp.newBuilder()
                .setSeconds(cat.getBirthday().toInstant().getEpochSecond())
                .setNanos(cat.getBirthday().toInstant().getNano())
                .build();

        return CatInfo.newBuilder()
                .setId(cat.getId())
                .setName(cat.getName())
                .setBirthday(birthday)
                .setOwnerId(0)
                .setTenantId(Int64Value.of(0))
                .setAvatarId(cat.getAvatarId())
                .build();
    }
}
