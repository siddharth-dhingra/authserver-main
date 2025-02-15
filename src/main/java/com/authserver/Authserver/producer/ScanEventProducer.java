package com.authserver.Authserver.producer;
import com.authserver.Authserver.dto.ScanRequestEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ScanEventProducer {

    @Value("${app.kafka.topics.scan}")
    private String scanTopic;

    private final KafkaTemplate<String, ScanRequestEvent> kafkaTemplate;

    public ScanEventProducer(KafkaTemplate<String, ScanRequestEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendScanEvent(ScanRequestEvent event) {
        kafkaTemplate.send(scanTopic, event);
    }
}
