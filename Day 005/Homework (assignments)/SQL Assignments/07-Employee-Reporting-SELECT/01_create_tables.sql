-- Employee Reporting - create database and tables

CREATE DATABASE employee_report_db;
USE employee_report_db;

-- departments table
CREATE TABLE departments (
    dept_id    INT AUTO_INCREMENT PRIMARY KEY,
    dept_name  VARCHAR(50) NOT NULL UNIQUE,
    location   VARCHAR(50) NOT NULL
);

-- employees table
CREATE TABLE employees (
    emp_id        INT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    phone         VARCHAR(15)  UNIQUE,
    dept_id       INT NOT NULL,
    job_title     VARCHAR(50)  NOT NULL,
    salary        DECIMAL(10,2) NOT NULL CHECK (salary > 0),
    joining_date  DATE NOT NULL,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- show tables
SHOW TABLES;
DESCRIBE departments;
DESCRIBE employees;
