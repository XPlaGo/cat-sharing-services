package ru.xplago.catservice.listeners;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.xplago.catservice.services.OwnerService;
import ru.xplago.catservice.services.dto.CreateOwnerDto;
import ru.xplago.catservice.topics.UserTopics;
import ru.xplago.common.kafka.models.user.UserActionModel;

@Configuration
@AllArgsConstructor(onConstructor_ = @__({@Autowired}))
public class UserListener {
    private OwnerService ownerService;

    private void observeUserActionModel(String id, UserActionModel model) {
        switch (model.getAction()) {
            case USER_CREATED -> ownerService.createOwner(
                    CreateOwnerDto.builder()
                            .userId(model.getUserId())
                            .build());
            case USER_DELETED -> ownerService.deleteByUserId(model.getUserId());
        }
    }

    @Bean
    public KStream<String, UserActionModel> userActionModelKStream(StreamsBuilder builder) {
        JsonSerde<UserActionModel> userActionModelJsonSerde = new JsonSerde<>(UserActionModel.class);
        KStream<String, UserActionModel> stream = builder
                .stream(UserTopics.USER_ACTION, Consumed.with(Serdes.String(), userActionModelJsonSerde));

        stream.foreach(this::observeUserActionModel);
        return stream;
    }
}
