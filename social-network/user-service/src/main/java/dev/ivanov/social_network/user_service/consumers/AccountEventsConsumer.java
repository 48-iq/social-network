package dev.ivanov.social_network.user_service.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivanov.social_network.user_service.config.KafkaConfig;
import dev.ivanov.social_network.user_service.events.AccountEvent;
import dev.ivanov.social_network.user_service.producers.AccountEventsProducer;
import dev.ivanov.social_network.user_service.services.UserService;
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
    private UserService userService;

    @Autowired
    private AccountEventsProducer accountEventsProducer;

    @KafkaListener(topics = KafkaConfig.ACCOUNT_EVENTS_TOPIC)
    public void handleAccountEvent(String accountEventJson) {
        try {
            AccountEvent accountEvent = objectMapper.readValue(accountEventJson, AccountEvent.class);
            if (!accountEvent.getCreatorId().equals(creatorId)) {
                try {
                    if (accountEvent.getAction().equals(AccountEvent.ACTION_CREATE)) {
                        userService.createUser(accountEvent.getAccountId());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    accountEventsProducer.send(AccountEvent.ACTION_DELETE, accountEvent.getAccountId());
                    throw e;
                }
                try {
                    if (accountEvent.getAction().equals(AccountEvent.ACTION_DELETE)) {
                        userService.deleteUser(accountEvent.getAccountId());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    accountEventsProducer.send(AccountEvent.ACTION_CREATE, accountEvent.getAccountId());
                    throw e;
                }
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
