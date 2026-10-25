package com.example.hospital.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/** A hospital department, e.g. Cardiology. Table: departments */
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    /**
     * One department has many doctors.
     * This is the INVERSE side: the foreign key department_id lives in the
     * doctors table and is controlled by Doctor.department (mappedBy).
     * LAZY: the doctors are not loaded when a department is loaded.
     */
    @OneToMany(mappedBy = "department")
    private List<Doctor> doctors = new ArrayList<>();

    /** Required by JPA. */
    protected Department() {
    }

    public Department(String name, String location) {
        this.name = name;
        this.location = location;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }
}
