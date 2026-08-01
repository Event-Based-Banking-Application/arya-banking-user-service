package org.arya.banking.user.outbox;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.arya.banking.common.model.OutboxEvent;
import org.arya.banking.common.model.OutboxStatus;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Document(collection = "user_outbox")
public class UserOutboxEvent extends OutboxEvent {

    @PersistenceCreator
    public UserOutboxEvent(String id, String aggregateId, String eventType, String payload,
                           String topic, OutboxStatus outboxStatus, int retryCount) {
        super(id, aggregateId, eventType, payload, topic, outboxStatus, retryCount);
    }
}
