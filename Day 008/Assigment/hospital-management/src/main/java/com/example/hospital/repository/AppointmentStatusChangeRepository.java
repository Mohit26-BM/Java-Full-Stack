package com.example.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hospital.entity.AppointmentStatusChange;

/** Extension: the audit trail of appointment status changes. */
public interface AppointmentStatusChangeRepository extends JpaRepository<AppointmentStatusChange, Long> {

    /** Derived query: ... WHERE appointment_id = ? ORDER BY changed_at, status_change_id (oldest first) */
    List<AppointmentStatusChange> findByAppointmentIdOrderByChangedAtAscIdAsc(Long appointmentId);
}
