package com.cabos.lambda_kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LambdaKafkaApplication {
    public static void main(String[] args) {
        SpringApplication.run(LambdaKafkaApplication.class, args);
        System.out.println("Lambda Kafka iniciada e aguardando mensagens...");
    }
}
