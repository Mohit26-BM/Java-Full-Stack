package com.example.hospital.mapper;

import com.example.hospital.dto.DoctorResponse;
import com.example.hospital.entity.Doctor;

/** Converts a Doctor entity into the DTO sent to the client. */
public final class DoctorMapper {

    private DoctorMapper() {
    }

    /** Reads doctor.getDepartment(): call inside a transaction (the department is LAZY). */
    public static DoctorResponse toResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getDoctorCode(),
                doctor.getName(),
                doctor.getSpecialization(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getConsultationFee(),
                doctor.getActive(),
                doctor.getDepartment().getId(),
                doctor.getDepartment().getName());
    }
}
