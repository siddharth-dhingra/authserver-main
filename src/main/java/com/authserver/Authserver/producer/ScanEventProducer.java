package com.authserver.Authserver.producer;
import com.authserver.Authserver.dto.ScanRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ScanEventProducer {

    @Value("${app.kafka.topics.jfc-unified}")
    private String unifiedTopic;

    @Value("${app.kafka.topics.scan-destination}")
    private String scanTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ScanEventProducer(@Qualifier("scanEventKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendScanEvent(ScanRequestEvent event) {
        // kafkaTemplate.send(scanTopic, event);
        if (event.getPayload() != null) {
            event.getPayload().setDestinationTopic(scanTopic);
        }
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(unifiedTopic, json);
        } catch (Exception e) {
            // Handle serialization exception appropriately
            e.printStackTrace();
        }
    }
}
