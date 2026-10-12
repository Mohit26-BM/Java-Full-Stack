package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.AuditEvent;

public interface AuditService {

    /*
    Validates and saves an event. Returns the generated event_id.
    */
    long logEvent(String eventType, String eventMessage)
            throws InvalidInputException;

    /*
    Returns null when not found.
    */
    AuditEvent getEvent(long eventId);

    List<AuditEvent> getRecentEvents(int limit);

    long countEvents();
}
