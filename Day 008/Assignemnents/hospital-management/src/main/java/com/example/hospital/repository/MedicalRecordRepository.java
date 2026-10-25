package com.example.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hospital.entity.MedicalRecord;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    /** Derived query on a nested property: ... WHERE appointment_id = ? */
    boolean existsByAppointmentId(Long appointmentId);
}
