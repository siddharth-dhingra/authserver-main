package com.authserver.Authserver.model;

import com.authserver.Authserver.model.FilterReferences.ToolType;

public class ScanEvent {
    private String tenantId;
    private ToolType toolType;
    private String destinationTopic;  // NEW field

    public ScanEvent() {
    }

    public ScanEvent(String tenantId, ToolType toolType, String destinationTopic) {
        this.tenantId = tenantId;
        this.toolType = toolType;
        this.destinationTopic = destinationTopic;
    }

    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public ToolType getToolType() {
        return toolType;
    }
    public void setToolType(ToolType toolType) {
        this.toolType = toolType;
    }

    public String getDestinationTopic() {
        return destinationTopic;
    }
    public void setDestinationTopic(String destinationTopic) {
        this.destinationTopic = destinationTopic;
    }

    @Override
    public String toString() {
        return "ScanEvent{" +
                "tenantId='" + tenantId + '\'' +
                ", toolType=" + toolType +
                ", destinationTopic='" + destinationTopic + '\'' +
                '}';
    }
}
