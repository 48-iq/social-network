package dev.ivanov.social_network.user_service.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivanov.social_network.user_service.config.KafkaConfig;
import dev.ivanov.social_network.user_service.events.AccountEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountEventsConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.kafka.creator-id}")
    private String creatorId;

    @KafkaListener(topics = KafkaConfig.ACCOUNT_EVENTS_TOPIC)
    public void handleAccountEvent(String accountEventJson) {
        try {
            AccountEvent accountEvent = objectMapper.readValue(accountEventJson, AccountEvent.class);
            if (!accountEvent.getCreatorId().equals(creatorId)) {
                if (accountEvent.getAction().equals(Actions.DELETE)) {

                }
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
