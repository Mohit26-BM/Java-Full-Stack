-- HR Dashboard - create database and tables

CREATE DATABASE hr_dashboard_db;
USE hr_dashboard_db;

-- departments table
CREATE TABLE departments (
    dept_id        INT AUTO_INCREMENT PRIMARY KEY,
    dept_name      VARCHAR(50) NOT NULL UNIQUE,
    location       VARCHAR(50) NOT NULL,
    annual_budget  DECIMAL(12,2) NOT NULL
);

-- employees table
CREATE TABLE employees (
    emp_id           INT AUTO_INCREMENT PRIMARY KEY,
    first_name       VARCHAR(50)  NOT NULL,
    last_name        VARCHAR(50)  NOT NULL,
    email            VARCHAR(100) NOT NULL UNIQUE,
    dept_id          INT NOT NULL,
    job_title        VARCHAR(50)  NOT NULL,
    job_level        ENUM('Junior', 'Mid', 'Senior', 'Manager') NOT NULL,
    employment_type  ENUM('Full-Time', 'Part-Time', 'Contract') NOT NULL DEFAULT 'Full-Time',
    salary           DECIMAL(10,2) NOT NULL CHECK (salary > 0),
    hire_date        DATE NOT NULL,
    status           ENUM('Active', 'On Leave', 'Resigned') NOT NULL DEFAULT 'Active',
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- show tables
SHOW TABLES;
DESCRIBE departments;
DESCRIBE employees;
