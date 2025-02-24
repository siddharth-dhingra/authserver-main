package com.authserver.Authserver.model;

public class UpdateTicketPayload {
    
    private String tenantId;
    private String ticketId;
    private String destinationTopic;
    
    public UpdateTicketPayload() {
    }

    public UpdateTicketPayload(String tenantId, String ticketId, String destinationTopic) {
        this.tenantId = tenantId;
        this.ticketId = ticketId;
        this.destinationTopic = destinationTopic;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getDestinationTopic() {
        return destinationTopic;
    }

    public void setDestinationTopic(String destinationTopic) {
        this.destinationTopic = destinationTopic;
    }
}
