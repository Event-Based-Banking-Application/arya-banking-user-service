package org.arya.banking.user.config;

import org.apache.avro.specific.SpecificRecord;
import org.arya.banking.common.kafka.config.KafkaConfiguration;
import org.arya.banking.common.mongo.config.MongoConfig;
import org.arya.banking.outbox.autoconfigure.OutboxProperties;
import org.arya.banking.outbox.kafka.OutboxEventProducer;
import org.arya.banking.outbox.service.OutBoxPublisherService;
import org.arya.banking.user.outbox.UserOutboxEvent;
import org.arya.banking.user.repository.UserOutboxEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

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

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> kafkaListenerContainerFactory(
            KafkaConfiguration kafkaConfiguration) {
        return kafkaConfiguration.kafkaListerFactory("user-service-group");
    }
}
