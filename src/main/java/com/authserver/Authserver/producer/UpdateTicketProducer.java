package com.authserver.Authserver.producer;

import com.authserver.Authserver.dto.UpdateTicketEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UpdateTicketProducer {

    @Value("${app.kafka.topics.jfc-unified}")
    private String unifiedTopic;

    @Value("${app.kafka.topics.ticket-destination}")
    private String updateTicketTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public UpdateTicketProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void updateTicketEvent(UpdateTicketEvent event) {
        // kafkaTemplate.send(scanTopic, event);
        if (event.getPayload() != null) {
            event.getPayload().setDestinationTopic(updateTicketTopic);
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