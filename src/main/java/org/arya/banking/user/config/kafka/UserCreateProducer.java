package org.arya.banking.user.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.common.avro.UserCreateEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreateProducer {

    private final KafkaTemplate<String, Object> userCreateEventTemplate;

    public void sendUserCreateEvent(UserCreateEvent userCreateEvent) {
        userCreateEventTemplate.send("user-create-event", userCreateEvent.getUserId().toString(), userCreateEvent);
        log.info("User create event sent for: {}", userCreateEvent.getUserId());
    }

}
