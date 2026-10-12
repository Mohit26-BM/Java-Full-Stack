-- Multi-Table Queries - create database and tables

CREATE DATABASE multi_table_db;
USE multi_table_db;

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
    job_title   VARCHAR(50)  NOT NULL,
    salary      DECIMAL(10,2) NOT NULL CHECK (salary > 0),
    dept_id     INT,
    manager_id  INT,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id),
    FOREIGN KEY (manager_id) REFERENCES employees(emp_id)
);

-- projects table
CREATE TABLE projects (
    project_id    INT AUTO_INCREMENT PRIMARY KEY,
    project_name  VARCHAR(100) NOT NULL UNIQUE,
    dept_id       INT NOT NULL,
    start_date    DATE NOT NULL,
    end_date      DATE,
    budget        DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- employee_projects table
CREATE TABLE employee_projects (
    emp_id          INT NOT NULL,
    project_id      INT NOT NULL,
    role            VARCHAR(20) NOT NULL DEFAULT 'Member',
    hours_per_week  INT NOT NULL,
    PRIMARY KEY (emp_id, project_id),
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

-- show tables
SHOW TABLES;
DESCRIBE departments;
DESCRIBE employees;
DESCRIBE projects;
DESCRIBE employee_projects;
