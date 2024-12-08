package dev.ivanov.social_network.user_service.producers;

import dev.ivanov.social_network.user_service.events.AccountEvent;
import dev.ivanov.social_network.user_service.events.FriendEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FriendEventsProducer {

    @Value("${app.kafka.creator-id}")
    private String creatorId;

    public void send(String firstUserId, String secondUserId, String action) {
        FriendEvent friendEvent = FriendEvent.builder()
                .action(action)
                .firstUserId(firstUserId)
                .secondUserId(secondUserId)
                .creatorId(creatorId)
                .build();


    }
}
