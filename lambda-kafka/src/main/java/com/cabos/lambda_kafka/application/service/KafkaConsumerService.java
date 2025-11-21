package com.cabos.lambda_kafka.application.service;

import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    public void process(String content) {
        System.out.println("Processando LAMBDA -> " + content);
    }
}
