-- =========================================================
-- Day 07 - Assignment 11: Request Body + DTO
-- Shared database Day_007_Assigments; this script only touches a11_ tables.
-- Run once before starting the application:
--   mysql -u root -p < schema.sql
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;
USE Day_007_Assigments;

DROP TABLE IF EXISTS a11_employees;

-- The API sends the address as a NESTED object; here it is stored flat.
CREATE TABLE a11_employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    department  VARCHAR(50)  NOT NULL,
    street      VARCHAR(150),
    city        VARCHAR(60),
    state       VARCHAR(60),
    pincode     CHAR(6),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_a11_employees_email UNIQUE (email)
);
