package com.authserver.Authserver.model;

public class CreateTicketPayload {
    
    private String tenantId;
    private String findingId;
    private String summary;
    private String description;
    private String destinationTopic;
    
    public CreateTicketPayload() {
    }

    public CreateTicketPayload(String tenantId, String findingId, String summary, String description, String destinationTopic) {
        this.tenantId = tenantId;
        this.findingId = findingId;
        this.summary = summary;
        this.description = description;
        this.destinationTopic = destinationTopic;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getFindingId() {
        return findingId;
    }

    public void setFindingId(String findingId) {
        this.findingId = findingId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDestinationTopic() {
        return destinationTopic;
    }

    public void setDestinationTopic(String destinationTopic) {
        this.destinationTopic = destinationTopic;
    }
}
