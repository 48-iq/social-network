package dev.ivanov.social_network.user_service.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import dev.ivanov.social_network.user_service.config.KafkaConfig;
import dev.ivanov.social_network.user_service.events.AccountEvent;
import dev.ivanov.social_network.user_service.exceptions.EventNotSentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class AccountEventsProducer {

    @Value("${app.kafka.creator-id}")
    private String creatorId;

    @Autowired
    private ObjectWriter objectWriter;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String action, String accountId) {

        AccountEvent accountEvent = AccountEvent.builder()
                .accountId(accountId)
                .action(action)
                .creatorId(creatorId)
                .build();
        try {
            String accountEventJson = objectWriter.writeValueAsString(accountEvent);

            kafkaTemplate.send(KafkaConfig.ACCOUNT_EVENTS_TOPIC, accountId,
                    accountEventJson).get();

        } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
            log.error(e.getMessage());
            throw new EventNotSentException("event with id " + accountId + " not sent");
        }
    }
}
