package com.authserver.Authserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.authserver.Authserver.CustomAnnotations.RequireRoles;
import com.authserver.Authserver.dto.JiraTicketDTO;
import com.authserver.Authserver.model.FilterReferences.RoleEnum;
import com.authserver.Authserver.service.JiraService;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequireRoles({RoleEnum.SUPER_ADMIN})
public class FetchTicketController {

    private final JiraService jiraService;

    public FetchTicketController(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @GetMapping
    public ResponseEntity<List<JiraTicketDTO>> getAllTickets(@RequestParam String tenantId) {
        List<JiraTicketDTO> ticketDTOs = jiraService.fetchTenantTickets(tenantId);
        return ResponseEntity.ok(ticketDTOs);
    }

    @GetMapping("/single")
    public ResponseEntity<JiraTicketDTO> getSingleTicket(
            @RequestParam String tenantId,
            @RequestParam String ticketId
    ) {
        JiraTicketDTO ticketDTO = jiraService.getSingleTicket(tenantId, ticketId);
        return ResponseEntity.ok(ticketDTO);
    }
}