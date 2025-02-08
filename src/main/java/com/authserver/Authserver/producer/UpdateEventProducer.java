package com.authserver.Authserver.producer;

import com.authserver.Authserver.model.UpdateEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UpdateEventProducer {

    @Value("${app.kafka.topics.update}")
    private String updateTopic;

    private final KafkaTemplate<String, UpdateEvent> updateEventKafkaTemplate;

    public UpdateEventProducer(KafkaTemplate<String, UpdateEvent> updateEventKafkaTemplate) {
        this.updateEventKafkaTemplate = updateEventKafkaTemplate;
    }

    public void sendUpdateEvent(UpdateEvent event) {
        // Key can be null or something like event.getRepo()
        updateEventKafkaTemplate.send(updateTopic, event);
    }
}