package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.AuditEvent;
import com.company.jdbc.repository.AuditEventRepository;

public class AuditServiceImpl implements AuditService {

    /*
    e.g. USER_LOGIN, FUNDS_TRANSFER
    */
    private static final String TYPE_PATTERN = "[A-Z0-9_]+";

    private final AuditEventRepository repository;

    public AuditServiceImpl(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public long logEvent(String eventType, String eventMessage)
            throws InvalidInputException {

        if (eventType == null || eventType.trim().isEmpty()) {
            throw new InvalidInputException("Event type cannot be blank.");
        }

        String type = eventType.trim().toUpperCase().replace(' ', '_');

        if (type.length() > 80) {
            throw new InvalidInputException("Event type cannot be longer than 80 characters.");
        }

        if (!type.matches(TYPE_PATTERN)) {
            throw new InvalidInputException(
                    "Event type can contain only letters, digits and underscores.");
        }

        String message =
                eventMessage == null || eventMessage.trim().isEmpty() ? null : eventMessage.trim();

        if (message != null && message.length() > 500) {
            throw new InvalidInputException("Event message cannot be longer than 500 characters.");
        }

        return repository.save(new AuditEvent(type, message));
    }

    @Override
    public AuditEvent getEvent(long eventId) {
        return repository.findById(eventId);
    }

    @Override
    public List<AuditEvent> getRecentEvents(int limit) {
        return repository.findRecent(limit);
    }

    @Override
    public long countEvents() {
        return repository.countAll();
    }
}
