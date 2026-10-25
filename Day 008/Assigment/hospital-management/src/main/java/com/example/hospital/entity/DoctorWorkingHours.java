package com.example.hospital.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Extension: the hours a doctor works on one weekday. Table: doctor_working_hours
 *
 * A doctor has at most one row per weekday (UNIQUE doctor_id + day_of_week).
 * The relationship is mapped only on this side (unidirectional @ManyToOne):
 * Doctor does not need a list of its working hours, and leaving it out means
 * loading a doctor never loads them by accident.
 */
@Entity
@Table(name = "doctor_working_hours",
       uniqueConstraints = @UniqueConstraint(name = "uq_working_hours_doctor_day", columnNames = {"doctor_id", "day_of_week"}))
public class DoctorWorkingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "working_hours_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 9)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /** Length of each generated appointment slot, in minutes. */
    @Column(name = "slot_minutes", nullable = false)
    private Integer slotMinutes;

    protected DoctorWorkingHours() {
    }

    public DoctorWorkingHours(Doctor doctor, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime,
                              Integer slotMinutes) {
        this.doctor = doctor;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotMinutes = slotMinutes;
    }

    /** Half-open, like appointments: the slot [start, end) must lie inside [startTime, endTime). */
    public boolean covers(LocalTime start, LocalTime end) {
        return !start.isBefore(startTime) && !end.isAfter(endTime);
    }

    public Long getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getSlotMinutes() {
        return slotMinutes;
    }
}
