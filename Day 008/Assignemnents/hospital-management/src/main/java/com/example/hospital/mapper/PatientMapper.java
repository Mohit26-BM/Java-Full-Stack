package com.example.hospital.mapper;

import com.example.hospital.dto.PatientResponse;
import com.example.hospital.entity.Patient;

/** Converts a Patient entity into the DTO sent to the client. */
public final class PatientMapper {

    private PatientMapper() {
    }

    public static PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getPatientNumber(),
                patient.getName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getAddress(),
                patient.getActive());
    }
}
