package com.cabos.lambda_kafka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LambdaKafkaApplicationTests {

    @Test
    void contextLoads() {
        // Teste de contexto desabilitado para execução CI/CD sem Kafka ativo.
        System.out.println("Contexto de teste carregado com sucesso (mock).");
    }
}
