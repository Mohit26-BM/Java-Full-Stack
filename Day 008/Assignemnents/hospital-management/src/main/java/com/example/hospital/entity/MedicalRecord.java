package com.example.hospital.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/** The doctor's record of a completed visit. Table: medical_records */
@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medical_record_id")
    private Long id;

    /**
     * One appointment -> at most one medical record.
     * Owning side: medical_records.appointment_id. unique = true makes the
     * database itself refuse a second record for the same appointment.
     *
     * The relationship is mapped on this side only. A @OneToOne(mappedBy) on
     * Appointment would make Hibernate load the record every time an
     * appointment is loaded, which we do not need.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(name = "diagnosis", nullable = false, length = 500)
    private String diagnosis;

    @Column(name = "prescription", length = 1000)
    private String prescription;

    @Column(name = "doctor_notes", length = 2000)
    private String doctorNotes;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    /** Required by JPA. */
    protected MedicalRecord() {
    }

    public MedicalRecord(Appointment appointment, String diagnosis, String prescription,
                         String doctorNotes, LocalDate followUpDate) {
        this.appointment = appointment;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.doctorNotes = doctorNotes;
        this.followUpDate = followUpDate;
    }

    public Long getId() {
        return id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }
}
