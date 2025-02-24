package com.authserver.Authserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.authserver.Authserver.CustomAnnotations.RequireRoles;
import com.authserver.Authserver.dto.UpdateTicketEvent;
import com.authserver.Authserver.model.UpdateTicketPayload;
import com.authserver.Authserver.model.FilterReferences.RoleEnum;
import com.authserver.Authserver.producer.UpdateTicketProducer;

@RestController
@RequestMapping("/tickets")
@RequireRoles({RoleEnum.SUPER_ADMIN})
public class TicketStatusController {

    private final UpdateTicketProducer updateTicketProducer;

    public TicketStatusController(UpdateTicketProducer updateTicketProducer) {
        this.updateTicketProducer = updateTicketProducer;
    }

    /**
     * Changes status from 'To Do' (or 'In Progress') directly to 'Done'.
     */
    @PostMapping("/done")
    public ResponseEntity<String> markTicketDone(
            @RequestParam String tenantId,
            @RequestParam String ticketId
    ) {
        UpdateTicketPayload payload = new UpdateTicketPayload(tenantId, ticketId, null);
        UpdateTicketEvent event = new UpdateTicketEvent(null, payload);
        updateTicketProducer.updateTicketEvent(event);
        return ResponseEntity.ok("Ticket " + ticketId + " status updated to Done");
    }
}