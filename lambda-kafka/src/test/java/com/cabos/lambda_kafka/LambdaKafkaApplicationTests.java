package com.cabos.lambda_kafka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Este teste é desabilitado para execução em CI/CD sem Kafka real.
// Serve apenas para validar que o build e o empacotamento estão corretos.
@SpringBootTest(classes = LambdaKafkaApplication.class)
@ActiveProfiles("test")
class LambdaKafkaApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("Contexto carregado em modo de teste sem Kafka ativo.");
    }
}
