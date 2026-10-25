package com.example.hospital.exception;

public class PatientInactiveException extends BusinessRuleException {

    private static final long serialVersionUID = 1L;

    public PatientInactiveException(String patientNumber) {
        super("PATIENT_INACTIVE", "Patient " + patientNumber + " is inactive and cannot book appointments");
    }
}
