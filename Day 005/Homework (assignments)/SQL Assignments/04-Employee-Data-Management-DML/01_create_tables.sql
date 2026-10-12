-- Employee Data Management - create database and tables

CREATE DATABASE employee_dml_db;
USE employee_dml_db;

-- departments table
CREATE TABLE departments (
    dept_id    INT AUTO_INCREMENT PRIMARY KEY,
    dept_name  VARCHAR(50) NOT NULL UNIQUE,
    location   VARCHAR(50) NOT NULL
);

-- employees table
CREATE TABLE employees (
    emp_id      INT AUTO_INCREMENT PRIMARY KEY,
    first_name  VARCHAR(50)  NOT NULL,
    last_name   VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    dept_id     INT NOT NULL,
    job_title   VARCHAR(50)  NOT NULL,
    salary      DECIMAL(10,2) NOT NULL CHECK (salary > 0),
    hire_date   DATE NOT NULL,
    status      ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- assets table
CREATE TABLE assets (
    asset_id    INT AUTO_INCREMENT PRIMARY KEY,
    asset_tag   VARCHAR(20) NOT NULL UNIQUE,
    asset_type  VARCHAR(30) NOT NULL,
    emp_id      INT,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id)
);

-- show tables
SHOW TABLES;
DESCRIBE departments;
DESCRIBE employees;
DESCRIBE assets;
