package com.example.hospital.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.PatientHistoryItem;
import com.example.hospital.dto.PatientRequest;
import com.example.hospital.dto.PatientResponse;
import com.example.hospital.dto.PatientUpdateRequest;
import com.example.hospital.service.PatientService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/** Module C (+ patient history from Module F) - /api/patients */
@Tag(name = "Patients", description = "Module C - patients and their medical history")
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /** Register a patient -> 201 Created */
    @PostMapping
    public ResponseEntity<PatientResponse> register(@Valid @RequestBody PatientRequest request) {
        PatientResponse created = patientService.registerPatient(request);
        return ResponseEntity.created(URI.create("/api/patients/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public PatientResponse findById(@PathVariable("id") Long id) {
        return patientService.findById(id);
    }

    @PutMapping("/{id}")
    public PatientResponse update(@PathVariable("id") Long id, @Valid @RequestBody PatientUpdateRequest request) {
        return patientService.updatePatient(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public PatientResponse deactivate(@PathVariable("id") Long id) {
        return patientService.deactivatePatient(id);
    }

    /** GET /api/patients/search?name=Raj   (or ?phone=9000000001, or ?email=...) */
    @GetMapping("/search")
    public List<PatientResponse> search(@RequestParam(name = "name", required = false) String name,
                                        @RequestParam(name = "phone", required = false) String phone,
                                        @RequestParam(name = "email", required = false) String email) {
        return patientService.searchPatients(name, phone, email);
    }

    /** Medical history: completed visits + medical records, newest first. */
    @GetMapping("/{id}/history")
    public List<PatientHistoryItem> history(@PathVariable("id") Long id) {
        return patientService.getMedicalHistory(id);
    }

    /** All appointments of the patient (any status), most recent first. */
    @GetMapping("/{id}/appointments")
    public List<AppointmentResponse> appointments(@PathVariable("id") Long id) {
        return patientService.getAppointments(id);
    }
}
