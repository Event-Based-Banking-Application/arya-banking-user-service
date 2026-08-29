package org.arya.banking.user.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.common.avro.LoginFailedEvent;
import org.arya.banking.common.utils.EventContext;
import org.arya.banking.user.dto.UpdateSecurityDetailsDto;
import org.arya.banking.user.service.SecurityDetailsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static org.arya.banking.common.constants.kafka.KafkaConstants.AUTH_FAILED_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListeners {

    private final SecurityDetailsService securityDetailsService;

    @KafkaListener(id = "login-failed-event", topics = AUTH_FAILED_TOPIC)
    public void onUserUpdateEvent(LoginFailedEvent event) {
        log.info("User update Event for userId: [{}] received", event.getUserId());
        EventContext.setEventContext(
                event.getMetadata().getCorrelationId().toString(),
                event.getMetadata().getEventId().toString()
        );
        UpdateSecurityDetailsDto updateSecurityDetailsDto = new UpdateSecurityDetailsDto(null, event.getIsLockUser());
        securityDetailsService.updateSecurityCredentials(event.getUserId().toString().toUpperCase(), updateSecurityDetailsDto);
    }
}
