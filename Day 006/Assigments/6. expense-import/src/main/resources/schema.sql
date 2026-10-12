-- =========================================================
-- Expense Import and Monthly Reporting - Schema
-- Run this first. WARNING: drops and recreates all tables
-- in the expense_reporting database only.
-- =========================================================

CREATE DATABASE IF NOT EXISTS expense_reporting;

USE expense_reporting;

DROP TABLE IF EXISTS expenses_benchmark;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS employees;

CREATE TABLE employees (
    employee_id   INT PRIMARY KEY AUTO_INCREMENT,
    employee_code VARCHAR(20) UNIQUE NOT NULL,
    employee_name VARCHAR(100) NOT NULL
);

CREATE TABLE expenses (
    expense_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id  INT NOT NULL,
    expense_date DATE NOT NULL,
    category     VARCHAR(60) NOT NULL,
    amount       DECIMAL(12,2) NOT NULL,
    description  VARCHAR(255),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- ---------------------------------------------------------
-- Extra table used ONLY by the optional benchmark
-- (batch vs one-row-at-a-time). Same columns as expenses,
-- so the benchmark never touches the real expense data
-- or the monthly reports.
-- ---------------------------------------------------------
CREATE TABLE expenses_benchmark (
    expense_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id  INT NOT NULL,
    expense_date DATE NOT NULL,
    category     VARCHAR(60) NOT NULL,
    amount       DECIMAL(12,2) NOT NULL,
    description  VARCHAR(255)
);
