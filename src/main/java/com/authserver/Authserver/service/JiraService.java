package com.authserver.Authserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.authserver.Authserver.dto.JiraTicketDTO;
import com.authserver.Authserver.model.Tenant;
import com.authserver.Authserver.model.TenantTicket;
import com.authserver.Authserver.repository.TenantRepository;
import com.authserver.Authserver.repository.TenantTicketRepository;


import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JiraService {

    private final TenantRepository tenantRepository;
    private final TenantTicketRepository tenantTicketRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${jira.api.base-path:/rest/api/2}")
    private String jiraApiBasePath;

    /**
     * We inject a WebClient.Builder to create a dedicated WebClient instance for this service.
     * You can configure timeouts, etc., if needed.
     */
    public JiraService(
            TenantRepository tenantRepository,
            TenantTicketRepository tenantTicketRepository,
            WebClient.Builder webClientBuilder
    ) {
        this.tenantRepository = tenantRepository;
        this.tenantTicketRepository = tenantTicketRepository;
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Fetches all Jira tickets for a given tenant.
     */
    public List<JiraTicketDTO> fetchTenantTickets(String tenantId) {
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Invalid tenantId: " + tenantId));

        // Collect all TenantTicket records for the given tenant
        List<TenantTicket> tenantTickets = tenantTicketRepository.findAll().stream()
                .filter(tt -> tt.getTenantId().equals(tenantId))
                .collect(Collectors.toList());

        List<JiraTicketDTO> ticketDTOs = new ArrayList<>();
        for (TenantTicket tt : tenantTickets) {
            String ticketId = tt.getTicketId();
            JiraTicketDTO ticketDTO = fetchTicketFromJira(tenant, ticketId);
            String fullFindingId = tt.getEsFindingId();
            String pureFindingId = fullFindingId;
            if (fullFindingId != null && fullFindingId.length() > 36) {
                pureFindingId = fullFindingId.substring(fullFindingId.length() - 36);
            }
            ticketDTO.setFindingId(pureFindingId);
            ticketDTOs.add(ticketDTO);
        }
        return ticketDTOs;
    }

    /**
     * Fetches a single Jira issue from Jira and maps it to a DTO.
     */
    private JiraTicketDTO fetchTicketFromJira(Tenant tenant, String ticketId) {
        String url = String.format("https://%s%s/issue/%s",
                tenant.getAccountUrl(), jiraApiBasePath, ticketId);

        String authString = tenant.getUsername() + ":" + tenant.getApiToken();
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody =
            webClientBuilder.build().get()
                            .uri(url)
                            .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .block();

            if (responseBody == null) {
                throw new RuntimeException("Jira ticket response was null");
            }

            String key = (String) responseBody.get("key");
            Map<String, Object> fields = (Map<String, Object>) responseBody.get("fields");

            Map<String, Object> issueType = (Map<String, Object>) fields.get("issuetype");
            String issueTypeName = (String) issueType.get("name");
            String issueDescription = (String) fields.get("description");

            String summary = (String) fields.get("summary");
            Map<String, Object> statusMap = (Map<String, Object>) fields.get("status");
            String statusName = (String) statusMap.get("name");

            return new JiraTicketDTO(key, issueTypeName, issueDescription, summary, statusName, null);

        } catch (WebClientResponseException wce) {
            throw new RuntimeException(
                    "Jira get issue API returned error: " + wce.getStatusCode() + " " + wce.getResponseBodyAsString(),
                    wce);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch Jira ticket: " + ex.getMessage(), ex);
        }
    }

    public JiraTicketDTO getSingleTicket(String tenantId, String ticketId) {
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Invalid tenantId: " + tenantId));

        JiraTicketDTO dto = fetchTicketFromJira(tenant, ticketId);
        Optional<TenantTicket> ttOpt = tenantTicketRepository.findByTicketId(ticketId);
        if (ttOpt.isPresent()) {
            String fullFindingId = ttOpt.get().getEsFindingId();
            String pureFindingId = fullFindingId;
            if (fullFindingId != null && fullFindingId.length() > 36) {
                pureFindingId = fullFindingId.substring(fullFindingId.length() - 36);
            }
            dto.setFindingId(pureFindingId);
        }
        return dto;
    }
}