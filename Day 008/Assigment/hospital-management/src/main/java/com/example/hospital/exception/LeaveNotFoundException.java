package com.example.hospital.exception;

public class LeaveNotFoundException extends NotFoundException {

    private static final long serialVersionUID = 1L;

    public LeaveNotFoundException(Long doctorId, Long leaveId) {
        super("LEAVE_NOT_FOUND", "Leave " + leaveId + " not found for doctor " + doctorId);
    }
}
