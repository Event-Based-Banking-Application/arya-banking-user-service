package org.arya.banking.user.config;

import org.arya.banking.common.config.MongoConfig;
import org.arya.banking.outbox.autoconfigure.OutboxProperties;
import org.arya.banking.outbox.kafka.OutboxEventProducer;
import org.arya.banking.outbox.service.OutBoxPublisherService;
import org.arya.banking.user.outbox.UserOutboxEvent;
import org.arya.banking.user.repository.UserOutboxEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MongoConfig.class)
public class UserServiceMongoConfig {

    @Bean
    public OutBoxPublisherService<UserOutboxEvent> outBoxPublisherService(
            UserOutboxEventRepository repository,
            OutboxEventProducer producer,
            OutboxProperties properties) {
        return new OutBoxPublisherService<>(repository, producer, properties);
    }

}
