package com.company.jdbc.model;

import java.time.LocalDateTime;

public class AuditEvent {

    private long eventId;
    private String eventType;
    private String eventMessage;
    private LocalDateTime createdAt;

    public AuditEvent() {
    }

    public AuditEvent(String eventType, String eventMessage) {
        this.eventType = eventType;
        this.eventMessage = eventMessage;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {

        return String.format(
                "%-6d %-19s %-18.18s %s",
                eventId,
                createdAt == null ? "-" : createdAt.toString().replace('T', ' '),
                eventType,
                eventMessage == null ? "-" : eventMessage
        );
    }
}
