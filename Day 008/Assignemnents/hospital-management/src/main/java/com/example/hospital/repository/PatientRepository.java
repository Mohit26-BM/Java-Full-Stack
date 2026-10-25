package com.example.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hospital.entity.Patient;

/** All methods are DERIVED QUERIES built from the method names. */
public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByPatientNumberIgnoreCase(String patientNumber);

    /** ... WHERE upper(name) LIKE upper('%' || ? || '%') ORDER BY name  (partial name search) */
    List<Patient> findByNameContainingIgnoreCaseOrderByNameAsc(String namePart);

    /** ... WHERE phone = ? */
    List<Patient> findByPhone(String phone);

    /** ... WHERE upper(email) = upper(?) */
    List<Patient> findByEmailIgnoreCase(String email);
}
