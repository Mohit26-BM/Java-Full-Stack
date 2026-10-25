package com.example.hospital.exception;

/** A new leave overlaps another leave, or the doctor still has appointments in that period. */
public class LeaveConflictException extends ConflictException {

    private static final long serialVersionUID = 1L;

    public LeaveConflictException(String errorCode, String message) {
        super(errorCode, message);
    }
}
