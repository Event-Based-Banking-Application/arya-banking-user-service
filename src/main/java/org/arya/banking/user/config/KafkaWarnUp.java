package org.arya.banking.user.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.common.avro.UserCreateEvent;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaWarnUp {

    private final ProducerFactory<String, Object> producerFactory;

    @PostConstruct
    public void init() {
        producerFactory.createProducer().close();
        log.info("Kafka connection established");
    }

}
