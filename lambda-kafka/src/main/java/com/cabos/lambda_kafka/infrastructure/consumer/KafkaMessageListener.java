package com.cabos.lambda_kafka.infrastructure.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.cabos.lambda_kafka.application.service.KafkaConsumerService;

@Component
public class KafkaMessageListener {

    private final KafkaConsumerService service;

    public KafkaMessageListener(KafkaConsumerService service) {
        this.service = service;
    }

    @KafkaListener(topics = "mensagens", groupId = "lambda-group")
    public void listen(String message) {
        service.process(message);
    }
}

