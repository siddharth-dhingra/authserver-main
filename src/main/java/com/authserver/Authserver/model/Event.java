package com.authserver.Authserver.model;

public interface Event<T> {
    String getEventId();
    EventTypes getType();
    T getPayload();
}