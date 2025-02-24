package com.authserver.Authserver.producer;
import com.authserver.Authserver.dto.CreateTicketEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CreateTicketProducer {

    @Value("${app.kafka.topics.jfc-unified}")
    private String unifiedTopic;

    @Value("${app.kafka.topics.ticket-destination}")
    private String createTicketTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public CreateTicketProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void createTicketEvent(CreateTicketEvent event) {
        // kafkaTemplate.send(scanTopic, event);
        if (event.getPayload() != null) {
            event.getPayload().setDestinationTopic(createTicketTopic);
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
