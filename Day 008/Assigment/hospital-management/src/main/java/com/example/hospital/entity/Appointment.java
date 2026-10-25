package com.example.hospital.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * An appointment of one patient with one doctor. Table: appointments
 *
 * Time model: the slot is HALF-OPEN [startTime, endTime).
 * 10:00-10:30 and 10:30-11:00 do not overlap (one ends exactly when the other starts).
 */
@Entity
@Table(name = "appointments")
@EntityListeners(AuditingEntityListener.class)      // fills createdAt / updatedAt
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long id;

    @Column(name = "appointment_number", nullable = false, unique = true, length = 50)
    private String appointmentNumber;

    /** Many appointments -> one patient. Owning side: appointments.patient_id */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /** Many appointments -> one doctor. Owning side: appointments.doctor_id */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status;

    @Column(name = "reason", length = 500)
    private String reason;

    /**
     * The doctor's fee COPIED at booking time. If the doctor's fee changes
     * later, this appointment still shows what the patient was quoted.
     */
    @Column(name = "fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal fee;

    /** Auditing: set once, on INSERT. */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Auditing: set on INSERT and on every UPDATE. */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Optimistic locking. Hibernate adds "AND version = ?" to every UPDATE and
     * increases the number. If another transaction changed the row first, the
     * UPDATE matches no row and Hibernate throws an exception instead of
     * silently overwriting the other change.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /** Required by JPA. */
    protected Appointment() {
    }

    /** A new appointment always starts as BOOKED. */
    public Appointment(String appointmentNumber, Patient patient, Doctor doctor, LocalDate appointmentDate,
                       LocalTime startTime, LocalTime endTime, String reason, BigDecimal fee) {
        this.appointmentNumber = appointmentNumber;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.fee = fee;
        this.status = AppointmentStatus.BOOKED;
    }

    public Long getId() {
        return id;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }
}
