package ru.xplago.catservice.listeners;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.xplago.catservice.services.OwnerService;
import ru.xplago.catservice.services.dto.CreateOwnerDto;
import ru.xplago.catservice.topics.UserTopics;
import ru.xplago.common.kafka.models.user.UserActionModel;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class UserListener {
    private OwnerService ownerService;

    @KafkaListener(topics = UserTopics.USER_ACTION, groupId = "cat")
    public void listenUserAction(UserActionModel model) {
        switch (model.getAction()) {
            case USER_CREATED -> ownerService.createOwner(
                    CreateOwnerDto.builder()
                            .userId(model.getUserId())
                            .build());
            case USER_DELETED -> ownerService.deleteByUserId(model.getUserId());
        }
    }
}
