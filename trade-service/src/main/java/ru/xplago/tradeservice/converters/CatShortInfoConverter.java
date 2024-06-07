package ru.xplago.tradeservice.converters;

import ru.xplago.common.grpc.cat.CatInfo;
import ru.xplago.common.grpc.trade.CatShortInfo;

public class CatShortInfoConverter {
    public static CatShortInfo convert(CatInfo catInfo) {
        CatShortInfo.Builder builder = CatShortInfo.newBuilder();
        builder.setId(catInfo.getId());
        builder.setName(catInfo.getName());
        builder.setBirthday(catInfo.getBirthday());
        builder.setOwnerId(catInfo.getOwnerId());
        builder.setAvatarId(catInfo.getAvatarId());
        return builder.build();
    }
}
