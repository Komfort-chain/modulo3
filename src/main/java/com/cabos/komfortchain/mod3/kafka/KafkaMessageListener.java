package com.cabos.komfortchain.mod3.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);
    @KafkaListener(
            topics = "${app.kafka.topic:lambda-topic}",
            groupId = "${spring.kafka.consumer.group-id:lambda-consumer-group}"
    )
    public void onMessage(ConsumerRecord<String, String> record) {
        String message = record.value();
        String logMessage = "A mensagem chegou: " + message;
        System.out.println(logMessage);           
        log.info(logMessage);                     
    }
}
