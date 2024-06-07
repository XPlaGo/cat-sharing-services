package ru.xplago.tradeservice.services;

import com.google.protobuf.Int64Value;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.common.grpc.cat.OwnerInfo;
import ru.xplago.common.grpc.cat.OwnerServiceGrpc;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OwnerService {
    private OwnerServiceGrpc.OwnerServiceBlockingStub stub;

    public OwnerInfo getOwnerByUserId(Long userId) {
        return stub.getOwnerByUserId(Int64Value.newBuilder().setValue(userId).build());
    }
}
