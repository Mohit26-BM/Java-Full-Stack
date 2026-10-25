package com.example.hospital.mapper;

import com.example.hospital.dto.MedicalRecordResponse;
import com.example.hospital.entity.MedicalRecord;

/** Converts a MedicalRecord entity into the DTO sent to the client. */
public final class MedicalRecordMapper {

    private MedicalRecordMapper() {
    }

    public static MedicalRecordResponse toResponse(MedicalRecord record) {
        return new MedicalRecordResponse(
                record.getId(),
                record.getAppointment().getId(),
                record.getAppointment().getAppointmentNumber(),
                record.getDiagnosis(),
                record.getPrescription(),
                record.getDoctorNotes(),
                record.getFollowUpDate());
    }
}
