package com.example.hospital.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.hospital.dto.DoctorSummary;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.entity.Doctor;

import jakarta.persistence.LockModeType;

/**
 * Doctor.department is LAZY. The list queries below are used to build
 * DoctorResponse objects, which need the department name. Without help this
 * would run one extra SELECT per doctor (the "N+1 problem").
 * @EntityGraph(attributePaths = "department") tells Spring Data to JOIN the
 * department into the same SELECT.
 */
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // ---------------- derived queries ----------------

    /** ... WHERE upper(specialization) = upper(?) */
    @EntityGraph(attributePaths = "department")
    List<Doctor> findBySpecializationIgnoreCase(String specialization);

    /** ... WHERE department_id = ? AND active = true */
    @EntityGraph(attributePaths = "department")
    List<Doctor> findByDepartmentIdAndActiveTrue(Long departmentId);

    /** ... WHERE consultation_fee BETWEEN ? AND ?  (both ends included) */
    @EntityGraph(attributePaths = "department")
    List<Doctor> findByConsultationFeeBetween(BigDecimal minFee, BigDecimal maxFee);

    /** ... WHERE active = true ORDER BY name */
    @EntityGraph(attributePaths = "department")
    List<Doctor> findByActiveTrueOrderByNameAsc();

    boolean existsByDoctorCodeIgnoreCase(String doctorCode);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    // ---------------- JPQL ----------------

    /**
     * DTO PROJECTION (section 16): only four columns are selected and turned
     * straight into DoctorSummary objects - no Doctor entities are loaded.
     */
    @Query("""
            SELECT new com.example.hospital.dto.DoctorSummary(d.id, d.name, d.specialization, d.consultationFee)
            FROM Doctor d
            WHERE d.active = true
            ORDER BY d.name
            """)
    List<DoctorSummary> findActiveDoctorSummaries();

    /**
     * Query 5 - AVAILABLE DOCTORS: active doctors of a specialization who have
     * NO appointment overlapping [startTime, endTime) on the given date.
     * Overlap rule (half-open slots): existing.start < new.end AND existing.end > new.start
     */
    @Query("""
            SELECT new com.example.hospital.dto.DoctorSummary(d.id, d.name, d.specialization, d.consultationFee)
            FROM Doctor d
            WHERE d.active = true
              AND LOWER(d.specialization) = LOWER(:specialization)
              AND NOT EXISTS (
                    SELECT a.id FROM Appointment a
                    WHERE a.doctor = d
                      AND a.appointmentDate = :date
                      AND a.status IN :blockingStatuses
                      AND a.startTime < :endTime
                      AND a.endTime > :startTime)
            ORDER BY d.name
            """)
    List<DoctorSummary> findAvailableDoctors(@Param("specialization") String specialization,
                                             @Param("date") LocalDate date,
                                             @Param("startTime") LocalTime startTime,
                                             @Param("endTime") LocalTime endTime,
                                             @Param("blockingStatuses") List<AppointmentStatus> blockingStatuses);

    /**
     * Loads the doctor and LOCKS the row (SELECT ... FOR UPDATE) until the
     * booking transaction ends. Two receptionists booking the same doctor at
     * the same moment are therefore handled one after the other, so both
     * cannot pass the overlap check before either has saved.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Doctor d WHERE d.id = :id")
    Optional<Doctor> findByIdForUpdate(@Param("id") Long id);
}
