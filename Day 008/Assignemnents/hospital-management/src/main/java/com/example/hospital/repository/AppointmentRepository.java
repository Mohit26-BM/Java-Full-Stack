package com.example.hospital.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.hospital.dto.DoctorWorkload;
import com.example.hospital.dto.PatientHistoryItem;
import com.example.hospital.entity.Appointment;
import com.example.hospital.entity.AppointmentStatus;

/**
 * Appointment.patient and Appointment.doctor are LAZY. Queries whose results
 * are turned into AppointmentResponse use "JOIN FETCH", which loads the
 * patient, doctor and department in the SAME SELECT (no extra queries).
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /** One appointment with its patient, doctor and department in one SELECT. */
    @Query("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient
            JOIN FETCH a.doctor d
            JOIN FETCH d.department
            WHERE a.id = :id
            """)
    Optional<Appointment> findDetailsById(@Param("id") Long id);

    /**
     * OVERLAP CHECK. Time slots are half-open: [startTime, endTime).
     * Two slots overlap when   existing.start < new.end   AND   existing.end > new.start.
     *
     * Existing 10:00-10:30:
     *   new 10:15-10:45 -> 10:00 < 10:45 and 10:30 > 10:15 -> overlap  (rejected)
     *   new 10:30-11:00 -> 10:00 < 11:00 but 10:30 > 10:30 is false -> no overlap (allowed)
     *   new 09:30-10:00 -> 10:00 < 10:00 is false -> no overlap (allowed)
     * Only "blocking" statuses count: a CANCELLED or NO_SHOW appointment frees the slot.
     */
    @Query("""
            SELECT COUNT(a) FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.appointmentDate = :date
              AND a.status IN :blockingStatuses
              AND a.startTime < :endTime
              AND a.endTime > :startTime
            """)
    long countOverlappingAppointments(@Param("doctorId") Long doctorId,
                                      @Param("date") LocalDate date,
                                      @Param("startTime") LocalTime startTime,
                                      @Param("endTime") LocalTime endTime,
                                      @Param("blockingStatuses") List<AppointmentStatus> blockingStatuses);

    /** Query 1 - DOCTOR SCHEDULE: a doctor's appointments on one date, by start time. */
    @Query("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient
            JOIN FETCH a.doctor d
            JOIN FETCH d.department
            WHERE d.id = :doctorId AND a.appointmentDate = :date
            ORDER BY a.startTime
            """)
    List<Appointment> findDoctorSchedule(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    /**
     * Query 2 - PATIENT HISTORY: COMPLETED appointments of a patient, newest first,
     * with the medical record if one exists (LEFT JOIN: visits without a record still appear).
     * Returned directly as PatientHistoryItem DTOs - only the needed columns are read.
     */
    @Query("""
            SELECT new com.example.hospital.dto.PatientHistoryItem(
                       a.id, a.appointmentNumber, a.appointmentDate, a.startTime,
                       d.name, d.specialization, a.fee,
                       mr.id, mr.diagnosis, mr.prescription, mr.followUpDate)
            FROM Appointment a
            JOIN a.doctor d
            LEFT JOIN MedicalRecord mr ON mr.appointment = a
            WHERE a.patient.id = :patientId
              AND a.status = com.example.hospital.entity.AppointmentStatus.COMPLETED
            ORDER BY a.appointmentDate DESC, a.startTime DESC
            """)
    List<PatientHistoryItem> findPatientHistory(@Param("patientId") Long patientId);

    /** All appointments of a patient (any status), most recent visit first. */
    @Query("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient p
            JOIN FETCH a.doctor d
            JOIN FETCH d.department
            WHERE p.id = :patientId
            ORDER BY a.appointmentDate DESC, a.startTime DESC
            """)
    List<Appointment> findPatientAppointments(@Param("patientId") Long patientId);

    /**
     * Query 6 - SEARCH WITH PAGINATION. Every filter is optional:
     * a null parameter switches its condition off.
     * Spring Data adds ORDER BY (from the Pageable's sort) and LIMIT/OFFSET.
     * countQuery is used for totalElements; it has no JOIN FETCH (nothing is loaded).
     */
    @Query(value = """
            SELECT a FROM Appointment a
            JOIN FETCH a.patient p
            JOIN FETCH a.doctor d
            JOIN FETCH d.department dep
            WHERE (:doctorId IS NULL OR d.id = :doctorId)
              AND (:patientId IS NULL OR p.id = :patientId)
              AND (:departmentId IS NULL OR dep.id = :departmentId)
              AND (:status IS NULL OR a.status = :status)
              AND (:date IS NULL OR a.appointmentDate = :date)
            """,
           countQuery = """
            SELECT COUNT(a) FROM Appointment a
            JOIN a.patient p
            JOIN a.doctor d
            JOIN d.department dep
            WHERE (:doctorId IS NULL OR d.id = :doctorId)
              AND (:patientId IS NULL OR p.id = :patientId)
              AND (:departmentId IS NULL OR dep.id = :departmentId)
              AND (:status IS NULL OR a.status = :status)
              AND (:date IS NULL OR a.appointmentDate = :date)
            """)
    Page<Appointment> search(@Param("doctorId") Long doctorId,
                             @Param("patientId") Long patientId,
                             @Param("departmentId") Long departmentId,
                             @Param("status") AppointmentStatus status,
                             @Param("date") LocalDate date,
                             Pageable pageable);

    /**
     * Query 3 - DEPARTMENT WORKLOAD: every doctor of a department and how many
     * (not cancelled) appointments they have in a date range. LEFT JOIN keeps
     * doctors with zero appointments in the result.
     */
    @Query("""
            SELECT new com.example.hospital.dto.DoctorWorkload(d.id, d.doctorCode, d.name, COUNT(a.id))
            FROM Doctor d
            LEFT JOIN Appointment a
                   ON a.doctor = d
                  AND a.appointmentDate BETWEEN :fromDate AND :toDate
                  AND a.status <> com.example.hospital.entity.AppointmentStatus.CANCELLED
            WHERE d.department.id = :departmentId
            GROUP BY d.id, d.doctorCode, d.name
            ORDER BY COUNT(a.id) DESC, d.name
            """)
    List<DoctorWorkload> findDepartmentWorkload(@Param("departmentId") Long departmentId,
                                                @Param("fromDate") LocalDate fromDate,
                                                @Param("toDate") LocalDate toDate);

    /** Query 4 (part 1) - derived count: ... WHERE status = ? AND appointment_date BETWEEN ? AND ? */
    long countByStatusAndAppointmentDateBetween(AppointmentStatus status, LocalDate fromDate, LocalDate toDate);

    /** Query 4 (part 2) - REVENUE: sum of the fees of COMPLETED appointments (0 when there are none). */
    @Query("""
            SELECT COALESCE(SUM(a.fee), 0)
            FROM Appointment a
            WHERE a.status = com.example.hospital.entity.AppointmentStatus.COMPLETED
              AND a.appointmentDate BETWEEN :fromDate AND :toDate
            """)
    BigDecimal sumCompletedRevenue(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}
