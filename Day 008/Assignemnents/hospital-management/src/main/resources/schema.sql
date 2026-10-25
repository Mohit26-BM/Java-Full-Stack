-- =====================================================================
-- Hospital Appointment & Patient Management - database schema
-- Run this first, then sample-data.sql:
--   mysql -u root -p < schema.sql
--   mysql -u root -p < sample-data.sql
-- (Re-running schema.sql deletes all data and starts again.)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS hospital_management;
USE hospital_management;

-- Drop children before parents (foreign keys)
DROP TABLE IF EXISTS medical_records;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS departments;

-- ---------------------------------------------------------------------
CREATE TABLE departments (
    department_id BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    location      VARCHAR(150),
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_departments_name UNIQUE (name)
);

-- ---------------------------------------------------------------------
-- Department 1 ---- * Doctor   (doctors.department_id -> departments)
CREATE TABLE doctors (
    doctor_id        BIGINT        AUTO_INCREMENT PRIMARY KEY,
    doctor_code      VARCHAR(10)   NOT NULL,
    name             VARCHAR(100)  NOT NULL,
    specialization   VARCHAR(100)  NOT NULL,
    email            VARCHAR(150)  NOT NULL,
    phone            VARCHAR(15)   NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL,
    active           BOOLEAN       NOT NULL DEFAULT TRUE,
    department_id    BIGINT        NOT NULL,
    CONSTRAINT uq_doctors_doctor_code UNIQUE (doctor_code),
    CONSTRAINT uq_doctors_email       UNIQUE (email),
    CONSTRAINT chk_doctors_fee        CHECK (consultation_fee > 0),
    CONSTRAINT fk_doctors_department  FOREIGN KEY (department_id) REFERENCES departments (department_id)
);

-- ---------------------------------------------------------------------
CREATE TABLE patients (
    patient_id     BIGINT       AUTO_INCREMENT PRIMARY KEY,
    patient_number VARCHAR(10)  NOT NULL,
    name           VARCHAR(100) NOT NULL,
    date_of_birth  DATE         NOT NULL,
    gender         VARCHAR(10)  NOT NULL,
    email          VARCHAR(150),
    phone          VARCHAR(15)  NOT NULL,
    address        VARCHAR(255),
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_patients_patient_number UNIQUE (patient_number)
);

-- ---------------------------------------------------------------------
-- Patient 1 ---- * Appointment * ---- 1 Doctor
CREATE TABLE appointments (
    appointment_id     BIGINT        AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(50)   NOT NULL,
    patient_id         BIGINT        NOT NULL,
    doctor_id          BIGINT        NOT NULL,
    appointment_date   DATE          NOT NULL,
    start_time         TIME          NOT NULL,
    end_time           TIME          NOT NULL,
    status             VARCHAR(20)   NOT NULL,
    reason             VARCHAR(500),
    fee                DECIMAL(10,2) NOT NULL,
    created_at         DATETIME      NOT NULL,
    updated_at         DATETIME      NOT NULL,
    version            BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT uq_appointments_number    UNIQUE (appointment_number),
    CONSTRAINT chk_appointments_times    CHECK (end_time > start_time),
    CONSTRAINT fk_appointments_patient   FOREIGN KEY (patient_id) REFERENCES patients (patient_id),
    CONSTRAINT fk_appointments_doctor    FOREIGN KEY (doctor_id)  REFERENCES doctors (doctor_id)
);

-- Speeds up the overlap check and the doctor schedule (doctor + date)
CREATE INDEX idx_appointments_doctor_date ON appointments (doctor_id, appointment_date);

-- ---------------------------------------------------------------------
-- Appointment 1 ---- 0..1 MedicalRecord   (UNIQUE appointment_id = at most one record)
CREATE TABLE medical_records (
    medical_record_id BIGINT        AUTO_INCREMENT PRIMARY KEY,
    appointment_id    BIGINT        NOT NULL,
    diagnosis         VARCHAR(500)  NOT NULL,
    prescription      VARCHAR(1000),
    doctor_notes      VARCHAR(2000),
    follow_up_date    DATE,
    CONSTRAINT uq_medical_records_appointment UNIQUE (appointment_id),
    CONSTRAINT fk_medical_records_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (appointment_id)
);
