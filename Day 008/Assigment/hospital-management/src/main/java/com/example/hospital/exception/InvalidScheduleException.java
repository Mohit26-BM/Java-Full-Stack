package com.example.hospital.exception;

/** The weekly timetable itself is invalid, for example the same weekday twice. */
public class InvalidScheduleException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public InvalidScheduleException(String message) {
        super("INVALID_SCHEDULE", message);
    }
}
