package com.authserver.Authserver.producer;

import com.authserver.Authserver.dto.UpdateAlertEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UpdateEventProducer {

    @Value("${app.kafka.topics.jfc-unified}")
    private String unifiedTopic;

    @Value("${app.kafka.topics.update-destination}")
    private String updateTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public UpdateEventProducer(@Qualifier("updateEventKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendUpdateEvent(UpdateAlertEvent event) {
        // Key can be null or something like event.getRepo()
        // kafkaTemplate.send(updateTopic, event);
        if (event.getPayload() != null) {
            event.getPayload().setDestinationTopic(updateTopic);
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