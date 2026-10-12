-- Capstone HR Management - create database and tables (DDL)

CREATE DATABASE hr_management_db;
USE hr_management_db;

-- departments table
CREATE TABLE departments (
    dept_id        INT AUTO_INCREMENT PRIMARY KEY,
    dept_name      VARCHAR(50) NOT NULL UNIQUE,
    location       VARCHAR(50) NOT NULL,
    annual_budget  DECIMAL(12,2) NOT NULL
);

-- jobs table
CREATE TABLE jobs (
    job_id      INT AUTO_INCREMENT PRIMARY KEY,
    job_title   VARCHAR(50) NOT NULL UNIQUE,
    min_salary  DECIMAL(10,2) NOT NULL,
    max_salary  DECIMAL(10,2) NOT NULL
);

-- employees table
CREATE TABLE employees (
    emp_id      INT AUTO_INCREMENT PRIMARY KEY,
    first_name  VARCHAR(50)  NOT NULL,
    last_name   VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    phone       VARCHAR(15)  UNIQUE,
    hire_date   DATE NOT NULL,
    job_id      INT NOT NULL,
    dept_id     INT NOT NULL,
    manager_id  INT,
    salary      DECIMAL(10,2) NOT NULL CHECK (salary > 0),
    status      ENUM('Active', 'On Leave', 'Terminated') NOT NULL DEFAULT 'Active',
    FOREIGN KEY (job_id) REFERENCES jobs(job_id),
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id),
    FOREIGN KEY (manager_id) REFERENCES employees(emp_id)
);

-- salary_history table
CREATE TABLE salary_history (
    history_id     INT AUTO_INCREMENT PRIMARY KEY,
    emp_id         INT NOT NULL,
    old_salary     DECIMAL(10,2) NOT NULL,
    new_salary     DECIMAL(10,2) NOT NULL,
    change_date    DATE NOT NULL,
    change_reason  VARCHAR(50) NOT NULL,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id)
);

-- leave_requests table
CREATE TABLE leave_requests (
    leave_id    INT AUTO_INCREMENT PRIMARY KEY,
    emp_id      INT NOT NULL,
    leave_type  ENUM('Annual', 'Sick', 'Unpaid') NOT NULL,
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL,
    status      ENUM('Pending', 'Approved', 'Rejected') NOT NULL DEFAULT 'Pending',
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id)
);

-- show tables
SHOW TABLES;
DESCRIBE departments;
DESCRIBE jobs;
DESCRIBE employees;
DESCRIBE salary_history;
DESCRIBE leave_requests;
