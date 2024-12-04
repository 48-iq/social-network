package dev.ivanov.social_network.auth_service.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import dev.ivanov.social_network.auth_service.config.KafkaConfig;
import dev.ivanov.social_network.auth_service.events.AccountEvent;
import dev.ivanov.social_network.auth_service.exceptions.EventNotSentException;
import dev.ivanov.social_network.auth_service.services.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class AccountEventsProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectWriter objectWriter;

    @Lazy
    @Autowired
    private AccountService accountService;

    @Value("${app.kafka.creator-id}")
    private String creatorId;

    public void send(String action, String accountId) {
        AccountEvent accountEvent = AccountEvent.builder()
                .accountId(accountId)
                .creatorId(creatorId)
                .action(action)
                .build();
        try {
            String jsonAccountEvent = objectWriter.writeValueAsString(accountEvent);
            kafkaTemplate.send(KafkaConfig.ACCOUNT_EVENTS_TOPIC, accountId, jsonAccountEvent)
                    .get();

        } catch (InterruptedException|ExecutionException|JsonProcessingException e) {
            log.error(e.getMessage());
            throw new EventNotSentException("account with id " + accountId + " not sent");
        }

    }
}
