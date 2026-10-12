-- =========================================================
-- Employee Directory - Schema
-- Run this first:  mysql -u root -p < schema.sql
-- WARNING: drops and recreates both tables.
-- =========================================================

CREATE DATABASE IF NOT EXISTS employee_directory;

USE employee_directory;

DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS departments;

CREATE TABLE departments (
    department_id   INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(80) UNIQUE NOT NULL
);

CREATE TABLE employees (
    employee_id   INT PRIMARY KEY AUTO_INCREMENT,
    employee_code VARCHAR(20) UNIQUE NOT NULL,
    employee_name VARCHAR(100) NOT NULL,
    email         VARCHAR(120) UNIQUE NOT NULL,
    phone         VARCHAR(20),
    department_id INT,
    salary        DECIMAL(12,2),
    joining_date  DATE,
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);
