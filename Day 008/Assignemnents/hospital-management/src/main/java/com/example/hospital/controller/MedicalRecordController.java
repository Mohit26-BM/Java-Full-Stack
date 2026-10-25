package com.example.hospital.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hospital.dto.MedicalRecordRequest;
import com.example.hospital.dto.MedicalRecordResponse;
import com.example.hospital.service.MedicalRecordService;

import jakarta.validation.Valid;

/** Module F - /api/medical-records */
@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    /** Create a medical record for a COMPLETED appointment -> 201 Created */
    @PostMapping
    public ResponseEntity<MedicalRecordResponse> create(@Valid @RequestBody MedicalRecordRequest request) {
        MedicalRecordResponse created = medicalRecordService.createMedicalRecord(request);
        return ResponseEntity.created(URI.create("/api/medical-records/" + created.id())).body(created);
    }
}
