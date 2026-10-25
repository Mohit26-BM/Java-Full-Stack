package com.example.hospital.repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.hospital.entity.DoctorWorkingHours;

/** Extension: a doctor's weekly working hours. */
public interface DoctorWorkingHoursRepository extends JpaRepository<DoctorWorkingHours, Long> {

    /** Derived query: ... WHERE doctor_id = ? */
    List<DoctorWorkingHours> findByDoctorId(Long doctorId);

    /** Derived query: ... WHERE doctor_id = ? AND day_of_week = ? */
    Optional<DoctorWorkingHours> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);

    /** Derived query: SELECT ... LIMIT 1 - "does this doctor have a timetable at all?" */
    boolean existsByDoctorId(Long doctorId);

    /**
     * Bulk DELETE in one statement (no entities loaded). Used before saving a
     * new weekly timetable. clearAutomatically empties the persistence context,
     * so no deleted rows stay cached in it.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM DoctorWorkingHours h WHERE h.doctor.id = :doctorId")
    int deleteAllForDoctor(@Param("doctorId") Long doctorId);
}
