package com.example.hospital.mapper;

import org.springframework.data.domain.Page;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.PageResponse;
import com.example.hospital.entity.Appointment;
import com.example.hospital.entity.Doctor;
import com.example.hospital.entity.Patient;

/** Converts Appointment entities into DTOs sent to the client. */
public final class AppointmentMapper {

    private AppointmentMapper() {
    }

    /**
     * Reads the patient, doctor and department: the appointment must have been
     * loaded with them (JOIN FETCH) or be used inside a transaction.
     */
    public static AppointmentResponse toResponse(Appointment appointment) {
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getAppointmentNumber(),
                patient.getId(),
                patient.getPatientNumber(),
                patient.getName(),
                doctor.getId(),
                doctor.getDoctorCode(),
                doctor.getName(),
                doctor.getDepartment().getName(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getReason(),
                appointment.getFee(),
                appointment.getVersion(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt());
    }

    /** A Spring Data Page of appointments -> PageResponse (content + page metadata). */
    public static PageResponse<AppointmentResponse> toPageResponse(Page<Appointment> page) {
        return new PageResponse<>(
                page.getContent().stream().map(AppointmentMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.getSort().toString());
    }
}
