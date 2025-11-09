package com.cabos.lambda_kafka.infrastructure.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageListener {

    @KafkaListener(topics = "mensagens", groupId = "lambda-group")
    public void listen(String message) {
        System.out.println("A mensagem chegou: " + message);
    }
}
