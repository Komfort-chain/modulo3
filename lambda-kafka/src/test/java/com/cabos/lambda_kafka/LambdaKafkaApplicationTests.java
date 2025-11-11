package com.cabos.lambda_kafka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092"
})
class LambdaKafkaApplicationTests {

    @Test
    void contextLoads() {
        // Apenas valida o contexto, sem tentar consumir
    }
}
