package com.cabos.lambda_kafka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest
@ActiveProfiles("test")
@Import(LambdaKafkaApplicationTests.KafkaDisabledConfig.class)
class LambdaKafkaApplicationTests {

    @Test
    void contextLoads() {
        // Teste de inicialização de contexto sem Kafka real
    }

    @Configuration
    static class KafkaDisabledConfig {
        // Força exclusão da auto-configuração do Kafka
        @org.springframework.context.annotation.Bean(name = "spring.kafka.bootstrap-servers")
        public String bootstrapServers() {
            return "dummy:9092";
        }
    }
}
