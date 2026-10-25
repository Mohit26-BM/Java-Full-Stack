package com.example.hospital.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
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

/**
 * Extension: one row of the appointment's audit trail. Table: appointment_status_changes
 *
 * Rows are only ever inserted, never updated or deleted, so there are no setters.
 * They are written in the same transaction as the status change they describe:
 * if the change is rolled back, so is its audit row.
 */
@Entity
@Table(name = "appointment_status_changes")
@EntityListeners(AuditingEntityListener.class)
public class AppointmentStatusChange {

    /** Who made a change. There is no login yet, so this records the channel, not a person. */
    public static final String BY_API = "API";
    public static final String BY_NO_SHOW_JOB = "NO_SHOW_JOB";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_change_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    /** null for the first row: the booking itself. */
    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 20)
    private AppointmentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 20)
    private AppointmentStatus toStatus;

    /** Auditing fills this on INSERT. */
    @CreatedDate
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Column(name = "changed_by", nullable = false, length = 50)
    private String changedBy;

    @Column(name = "note", length = 255)
    private String note;

    protected AppointmentStatusChange() {
    }

    public AppointmentStatusChange(Appointment appointment, AppointmentStatus fromStatus, AppointmentStatus toStatus,
                                   String changedBy, String note) {
        this.appointment = appointment;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public AppointmentStatus getFromStatus() {
        return fromStatus;
    }

    public AppointmentStatus getToStatus() {
        return toStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public String getNote() {
        return note;
    }
}
