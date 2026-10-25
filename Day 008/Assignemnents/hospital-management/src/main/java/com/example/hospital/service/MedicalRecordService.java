package com.example.hospital.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hospital.dto.MedicalRecordRequest;
import com.example.hospital.dto.MedicalRecordResponse;
import com.example.hospital.entity.Appointment;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.entity.MedicalRecord;
import com.example.hospital.exception.AppointmentNotFoundException;
import com.example.hospital.exception.InvalidAppointmentStatusException;
import com.example.hospital.exception.InvalidMedicalRecordException;
import com.example.hospital.exception.MedicalRecordAlreadyExistsException;
import com.example.hospital.mapper.MedicalRecordMapper;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.MedicalRecordRepository;

/** Module F - Medical records. */
@Service
@Transactional(readOnly = true)
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                AppointmentRepository appointmentRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /** POST /api/medical-records - record for an appointment that is already COMPLETED. */
    @Transactional
    public MedicalRecordResponse createMedicalRecord(MedicalRecordRequest request) {
        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new AppointmentNotFoundException(request.appointmentId()));
        MedicalRecord record = createRecord(appointment, request.diagnosis(), request.prescription(),
                request.doctorNotes(), request.followUpDate());
        return MedicalRecordMapper.toResponse(record);
    }

    /**
     * The rules for a medical record. Also called by AppointmentService.completeAppointment():
     * there it runs INSIDE the caller's transaction (propagation REQUIRED, the default),
     * so a failure here also undoes the caller's status change.
     */
    @Transactional
    public MedicalRecord createRecord(Appointment appointment, String diagnosis, String prescription,
                                      String doctorNotes, LocalDate followUpDate) {
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentStatusException("A medical record can only be created for a COMPLETED"
                    + " appointment; " + appointment.getAppointmentNumber() + " is " + appointment.getStatus());
        }
        if (medicalRecordRepository.existsByAppointmentId(appointment.getId())) {
            throw new MedicalRecordAlreadyExistsException(appointment.getAppointmentNumber());
        }
        if (followUpDate != null && !followUpDate.isAfter(appointment.getAppointmentDate())) {
            throw new InvalidMedicalRecordException("followUpDate " + followUpDate
                    + " must be after the appointment date " + appointment.getAppointmentDate());
        }
        MedicalRecord record = new MedicalRecord(appointment, diagnosis.trim(), prescription, doctorNotes, followUpDate);
        return medicalRecordRepository.save(record);
    }
}
