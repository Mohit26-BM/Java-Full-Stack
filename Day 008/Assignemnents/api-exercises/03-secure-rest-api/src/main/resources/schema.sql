-- =====================================================================
-- Exercise 3 - Secure REST API (employee portal)
-- Run this first, then sample-data.sql:
--   mysql -u root -p < schema.sql
--   mysql -u root -p < sample-data.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS employee_portal;
USE employee_portal;

DROP TABLE IF EXISTS employees;

CREATE TABLE employees (
    employee_id BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL,
    department  VARCHAR(50)   NOT NULL,
    designation VARCHAR(100)  NOT NULL,
    salary      DECIMAL(12,2) NOT NULL,       -- payroll data: visible to HR and ADMIN only
    status      VARCHAR(20)   NOT NULL,
    CONSTRAINT uq_employees_email UNIQUE (email)
);
