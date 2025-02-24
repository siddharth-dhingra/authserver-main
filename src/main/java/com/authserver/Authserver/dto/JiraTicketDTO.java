package com.authserver.Authserver.dto;

public class JiraTicketDTO {

    private String ticketId;             
    private String issueTypeName;        
    private String issueTypeDescription; 
    private String summary;              
    private String statusName;   
    private String findingId;        

    // Constructors
    public JiraTicketDTO() {}

    public JiraTicketDTO(String ticketId, String issueTypeName, String issueTypeDescription,
                         String summary, String statusName, String findingId) {
        this.ticketId = ticketId;
        this.issueTypeName = issueTypeName;
        this.issueTypeDescription = issueTypeDescription;
        this.summary = summary;
        this.statusName = statusName;
        this.findingId = findingId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getIssueTypeName() {
        return issueTypeName;
    }

    public void setIssueTypeName(String issueTypeName) {
        this.issueTypeName = issueTypeName;
    }

    public String getIssueTypeDescription() {
        return issueTypeDescription;
    }

    public void setIssueTypeDescription(String issueTypeDescription) {
        this.issueTypeDescription = issueTypeDescription;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getFindingId() {
        return findingId;
    }

    public void setFindingId(String findingId) {
        this.findingId = findingId;
    }    
}