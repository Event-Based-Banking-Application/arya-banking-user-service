package org.arya.banking.user.config;

import org.arya.banking.common.avro.UserCreateEvent;
import org.arya.banking.common.config.MongoConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Import(MongoConfig.class)
public class UserServiceMongoConfig {

    @Bean
    public KafkaTemplate<String, UserCreateEvent> userCreateTemplate(ProducerFactory<String, UserCreateEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}
