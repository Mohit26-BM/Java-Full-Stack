package com.company.jdbc.repository;

import java.util.List;

import com.company.jdbc.model.AuditEvent;

public interface AuditEventRepository {

    /*
    Returns the generated event_id.
    */
    long save(AuditEvent event);

    /*
    Returns null when not found.
    */
    AuditEvent findById(long eventId);

    /*
    Newest first.
    */
    List<AuditEvent> findRecent(int limit);

    long countAll();
}
