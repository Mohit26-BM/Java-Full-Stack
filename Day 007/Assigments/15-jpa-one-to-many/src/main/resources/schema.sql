-- =========================================================
-- Day 07 - Assignment 15: JPA One-to-Many Relationship
-- Shared database Day_007_Assigments; this script only touches a15_ tables.
-- Run once before starting the application:
--   mysql -u root -p < schema.sql
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;
USE Day_007_Assigments;

-- child table first (it references the parent)
DROP TABLE IF EXISTS a15_employees;
DROP TABLE IF EXISTS a15_departments;

-- ONE side
CREATE TABLE a15_departments (
    department_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(80) NOT NULL,
    location      VARCHAR(80),
    CONSTRAINT uq_a15_departments_name UNIQUE (name)
);

-- MANY side: holds the foreign key -> the OWNING side in JPA
CREATE TABLE a15_employees (
    employee_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL,
    salary        DECIMAL(10,2) NOT NULL,
    department_id BIGINT        NOT NULL,
    CONSTRAINT uq_a15_employees_email UNIQUE (email),
    CONSTRAINT fk_a15_employees_department FOREIGN KEY (department_id)
        REFERENCES a15_departments (department_id)
        -- no ON DELETE CASCADE: deleting children is done by JPA (cascade = ALL)
);

INSERT INTO a15_departments (name, location) VALUES
    ('Engineering', 'Bengaluru'),
    ('Finance',     'Mumbai'),
    ('HR',          'Pune');

INSERT INTO a15_employees (name, email, salary, department_id) VALUES
    ('Asha Rao',     'asha@company.com',  95000.00, 1),
    ('Ravi Kumar',   'ravi@company.com',  88000.00, 1),
    ('Meera Iyer',   'meera@company.com', 72000.00, 2),
    ('Kiran Das',    'kiran@company.com', 65000.00, 2),
    ('Neha Singh',   'neha@company.com',  60000.00, 3);
