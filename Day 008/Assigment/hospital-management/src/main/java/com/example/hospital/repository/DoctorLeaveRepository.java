package com.example.hospital.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hospital.entity.DoctorLeave;

/**
 * Extension: doctor leave. Leave ranges are INCLUSIVE (startDate..endDate).
 *
 * Two inclusive ranges [s1, e1] and [s2, e2] overlap when  s1 <= e2 AND e1 >= s2.
 * A single day d is "on leave" when  startDate <= d AND endDate >= d  (the same rule with s2 = e2 = d).
 */
public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, Long> {

    /** Derived query: ... WHERE doctor_id = ? ORDER BY start_date */
    List<DoctorLeave> findByDoctorIdOrderByStartDate(Long doctorId);

    /** Is the doctor on leave on this date?  ... WHERE doctor_id = ? AND start_date <= ? AND end_date >= ? */
    Optional<DoctorLeave> findFirstByDoctorIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long doctorId, LocalDate date, LocalDate sameDate);

    /** Does a new leave [from, to] overlap an existing one?  start_date <= to AND end_date >= from */
    boolean existsByDoctorIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long doctorId, LocalDate to, LocalDate from);

    Optional<DoctorLeave> findByIdAndDoctorId(Long id, Long doctorId);
}
