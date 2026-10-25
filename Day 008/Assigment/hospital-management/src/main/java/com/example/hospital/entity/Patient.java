package com.example.hospital.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/** A registered patient. Table: patients */
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    @Column(name = "patient_number", nullable = false, unique = true, length = 10)
    private String patientNumber;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /** EnumType.STRING stores "MALE" instead of the position number 0. */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    /** Patients are deactivated, never deleted, so their history is kept. */
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    /** One patient has many appointments (inverse side, see Appointment.patient). */
    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments = new ArrayList<>();

    /** Required by JPA. */
    protected Patient() {
    }

    public Patient(String patientNumber, String name, LocalDate dateOfBirth, Gender gender,
                   String email, String phone, String address) {
        this.patientNumber = patientNumber;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}
