package com.authserver.Authserver.producer;

import com.authserver.Authserver.dto.UpdateAlertEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UpdateEventProducer {

    @Value("${app.kafka.topics.update}")
    private String updateTopic;

    private final KafkaTemplate<String, UpdateAlertEvent> updateEventKafkaTemplate;

    public UpdateEventProducer(KafkaTemplate<String, UpdateAlertEvent> updateEventKafkaTemplate) {
        this.updateEventKafkaTemplate = updateEventKafkaTemplate;
    }

    public void sendUpdateEvent(UpdateAlertEvent event) {
        // Key can be null or something like event.getRepo()
        updateEventKafkaTemplate.send(updateTopic, event);
    }
}