package com.example.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.PatientHistoryItem;
import com.example.hospital.dto.PatientRequest;
import com.example.hospital.dto.PatientResponse;
import com.example.hospital.dto.PatientUpdateRequest;
import com.example.hospital.entity.Patient;
import com.example.hospital.exception.DuplicatePatientException;
import com.example.hospital.exception.InvalidSearchCriteriaException;
import com.example.hospital.exception.PatientNotFoundException;
import com.example.hospital.mapper.AppointmentMapper;
import com.example.hospital.mapper.PatientMapper;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.PatientRepository;

/** Module C - Patient management (+ the patient's visits from Module F). */
@Service
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /** Registers a patient. The patient number must be unique. */
    @Transactional
    public PatientResponse registerPatient(PatientRequest request) {
        String patientNumber = request.patientNumber().trim().toUpperCase();
        if (patientRepository.existsByPatientNumberIgnoreCase(patientNumber)) {
            throw new DuplicatePatientException(patientNumber);
        }
        Patient patient = new Patient(patientNumber, request.name().trim(), request.dateOfBirth(),
                request.gender(), request.email(), request.phone(), request.address());
        Patient saved = patientRepository.save(patient);
        return PatientMapper.toResponse(saved);
    }

    @Transactional
    public PatientResponse updatePatient(Long id, PatientUpdateRequest request) {
        Patient patient = getPatient(id);
        patient.setName(request.name().trim());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setEmail(request.email());
        patient.setPhone(request.phone());
        patient.setAddress(request.address());
        return PatientMapper.toResponse(patient);
    }

    /**
     * Deactivates instead of deleting: the patient's appointments and medical
     * records stay in the database and remain visible in the history.
     */
    @Transactional
    public PatientResponse deactivatePatient(Long id) {
        Patient patient = getPatient(id);
        patient.setActive(false);
        return PatientMapper.toResponse(patient);
    }

    public PatientResponse findById(Long id) {
        return PatientMapper.toResponse(getPatient(id));
    }

    /** Search by exactly one of: partial name, phone, email. */
    public List<PatientResponse> searchPatients(String name, String phone, String email) {
        int filters = 0;
        if (name != null) {
            filters++;
        }
        if (phone != null) {
            filters++;
        }
        if (email != null) {
            filters++;
        }
        if (filters != 1) {
            throw new InvalidSearchCriteriaException("Provide exactly one of: name, phone, email");
        }

        List<Patient> patients;
        if (name != null) {
            patients = patientRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name.trim());
        } else if (phone != null) {
            patients = patientRepository.findByPhone(phone.trim());
        } else {
            patients = patientRepository.findByEmailIgnoreCase(email.trim());
        }
        return patients.stream().map(PatientMapper::toResponse).toList();
    }

    /** Module F - medical history: completed visits with their medical records, newest first. */
    public List<PatientHistoryItem> getMedicalHistory(Long patientId) {
        getPatient(patientId);                              // 404 if the patient does not exist
        return appointmentRepository.findPatientHistory(patientId);
    }

    /** All appointments of the patient (any status), most recent visit first. */
    public List<AppointmentResponse> getAppointments(Long patientId) {
        getPatient(patientId);
        return appointmentRepository.findPatientAppointments(patientId).stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }

    private Patient getPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }
}
