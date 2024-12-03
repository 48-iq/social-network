package dev.ivanov.social_network.auth_service.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivanov.social_network.auth_service.config.KafkaConfig;
import dev.ivanov.social_network.auth_service.events.AccountEvent;
import dev.ivanov.social_network.auth_service.services.AccountService;
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

    @Autowired
    private AccountService accountService;

    @KafkaListener(topics = KafkaConfig.ACCOUNT_EVENTS_TOPIC)
    public void handleAccountEvent(String jsonAccountEvent) {
        try {
            AccountEvent accountEvent = objectMapper.readValue(jsonAccountEvent, AccountEvent.class);
            if (!accountEvent.getCreatorId().equals(creatorId)) {
                if (accountEvent.getAction().equals("delete")) {
                    accountService.deleteAccount(accountEvent.getAccountId());
                }
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }
}
