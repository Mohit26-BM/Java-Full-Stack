-- =========================================================
-- Employee Performance - Schema
-- Run order: 1. schema.sql  2. procedure.sql  3. sample-data.sql
-- WARNING: drops and recreates all tables in employee_performance.
-- =========================================================

CREATE DATABASE IF NOT EXISTS employee_performance;

USE employee_performance;

DROP TABLE IF EXISTS performance_reviews;
DROP TABLE IF EXISTS rating_bands;
DROP TABLE IF EXISTS employees;

-- ---------------------------------------------------------
-- Added (not in the assignment's design): lets the app show
-- names and tell "unknown employee" apart from
-- "employee with no reviews".
-- ---------------------------------------------------------
CREATE TABLE employees (
    employee_id   INT PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL
);

-- ---------------------------------------------------------
-- Table from the assignment.
-- review_period format used by this project: YYYY-Qn (e.g. 2026-Q2).
-- That format sorts correctly as text, so MAX(review_period)
-- is the latest period.
-- ---------------------------------------------------------
CREATE TABLE performance_reviews (
    review_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id   INT NOT NULL,
    review_period VARCHAR(20) NOT NULL,
    rating        DECIMAL(4,2) NOT NULL,
    reviewer      VARCHAR(120) NOT NULL,
    comments      VARCHAR(500)
);

-- ---------------------------------------------------------
-- Optional challenge: CONFIGURABLE rating bands.
-- An average rating belongs to the band with the highest
-- min_rating that is <= the average.
-- Change the thresholds here (or from the app, menu 5);
-- the stored procedure does not need to change.
-- ---------------------------------------------------------
CREATE TABLE rating_bands (
    band_name  VARCHAR(30) PRIMARY KEY,
    min_rating DECIMAL(4,2) NOT NULL UNIQUE
);
