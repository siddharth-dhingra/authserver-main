package com.authserver.Authserver.dto;

import java.util.UUID;
import com.authserver.Authserver.model.Event;
import com.authserver.Authserver.model.EventTypes;
import com.authserver.Authserver.model.UpdateEvent;

public class UpdateAlertEvent implements Event<UpdateEvent> {

    private String eventId;
    public static final EventTypes TYPE = EventTypes.UPDATE_FINDING; 
    private UpdateEvent payload;

    public UpdateAlertEvent() {}

    public UpdateAlertEvent(String eventId, UpdateEvent payload) {
        this.eventId = (eventId == null || eventId.isEmpty()) 
            ? UUID.randomUUID().toString() : eventId;
        this.payload = payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public EventTypes getType() {
        return TYPE;
    }

    @Override
    public UpdateEvent getPayload() {
        return payload;
    }

    public void setPayload(UpdateEvent payload) {
        this.payload = payload;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}