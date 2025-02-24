package com.authserver.Authserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.authserver.Authserver.CustomAnnotations.RequireRoles;
import com.authserver.Authserver.dto.CreateTicketEvent;
import com.authserver.Authserver.model.CreateTicketPayload;
import com.authserver.Authserver.model.FilterReferences.RoleEnum;
import com.authserver.Authserver.producer.CreateTicketProducer;

@RestController
@RequestMapping("/tickets")
@RequireRoles({RoleEnum.SUPER_ADMIN})
public class CreateTicketController {

    private final CreateTicketProducer createTicketProducer;

    public CreateTicketController(CreateTicketProducer createTicketProducer) {
        this.createTicketProducer = createTicketProducer;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTicket(
            @RequestParam String tenantId,
            @RequestParam String findingId,
            @RequestParam String summary,
            @RequestParam String description
    ) {
        CreateTicketPayload payload = new CreateTicketPayload(tenantId, findingId, summary, description,null);
        CreateTicketEvent event = new CreateTicketEvent(payload, null);

        createTicketProducer.createTicketEvent(event);
        return ResponseEntity.ok("Jira ticket created successfully with ID: ");
    }
}