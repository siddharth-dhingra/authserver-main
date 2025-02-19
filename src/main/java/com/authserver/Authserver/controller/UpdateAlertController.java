package com.authserver.Authserver.controller;

import com.authserver.Authserver.CustomAnnotations.RequireRoles;
import com.authserver.Authserver.dto.UpdateAlertEvent;
import com.authserver.Authserver.model.UpdateEvent;
import com.authserver.Authserver.model.FilterReferences.RoleEnum;
import com.authserver.Authserver.producer.UpdateEventProducer;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequireRoles({RoleEnum.SUPER_ADMIN})
@RequestMapping("/alerts")
public class UpdateAlertController {

    private final UpdateEventProducer updateEventProducer;

    public UpdateAlertController(UpdateEventProducer updateEventProducer) {
        this.updateEventProducer = updateEventProducer;
    }

    
    @PostMapping("/update")
    public ResponseEntity<String> updateAlert(@RequestBody UpdateEvent request, @RequestParam String tenantId) {

        request.setTenantId(tenantId);
        UpdateAlertEvent event = new UpdateAlertEvent(UUID.randomUUID().toString(), request);
        updateEventProducer.sendUpdateEvent(event);
        return ResponseEntity.ok("Update request published to Kafka successfully.");
    }
}
