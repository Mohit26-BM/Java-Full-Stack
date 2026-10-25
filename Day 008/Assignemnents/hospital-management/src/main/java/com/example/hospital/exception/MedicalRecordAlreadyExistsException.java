package com.example.hospital.exception;

public class MedicalRecordAlreadyExistsException extends ConflictException {

    private static final long serialVersionUID = 1L;

    public MedicalRecordAlreadyExistsException(String appointmentNumber) {
        super("MEDICAL_RECORD_ALREADY_EXISTS",
                "Appointment " + appointmentNumber + " already has a medical record");
    }
}
