package ru.xplago.tradeservice.services;

import com.google.protobuf.Int64Value;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.xplago.common.grpc.cat.CatInfo;
import ru.xplago.common.grpc.cat.CatServiceGrpc;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CatService {
    private CatServiceGrpc.CatServiceBlockingStub stub;

    public CatInfo getCatInfo(Long id) {
        return stub.getCatById(Int64Value.newBuilder().setValue(id).build());
    }
}
