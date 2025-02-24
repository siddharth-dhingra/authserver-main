package com.authserver.Authserver.dto;

import java.util.UUID;

import com.authserver.Authserver.model.CreateTicketPayload;
import com.authserver.Authserver.model.Event;
import com.authserver.Authserver.model.EventTypes;

public class CreateTicketEvent implements Event<CreateTicketPayload> {
    
    private String eventId;
    public static EventTypes TYPE = EventTypes.TICKETING_CREATE;
    private CreateTicketPayload payload;

    public CreateTicketEvent(CreateTicketPayload payload, String eventId) {
        this.payload = payload;
        this.eventId = (eventId == null || eventId.isEmpty()) ? UUID.randomUUID().toString() : eventId;
    }

    public CreateTicketEvent() {}

    @Override
    public CreateTicketPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public EventTypes getType() {
        return TYPE;
    }

    public void setPayload(CreateTicketPayload payload) {
        this.payload = payload;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public static void setTYPE(EventTypes tYPE) {
        TYPE = tYPE;
    }
}
