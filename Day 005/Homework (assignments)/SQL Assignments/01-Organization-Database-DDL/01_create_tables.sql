-- Organization Database - create database and tables

CREATE DATABASE org_db;
USE org_db;

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
    phone       VARCHAR(15)  UNIQUE,
    dept_id     INT NOT NULL,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- projects table
CREATE TABLE projects (
    project_id    INT AUTO_INCREMENT PRIMARY KEY,
    project_name  VARCHAR(100) NOT NULL UNIQUE,
    dept_id       INT NOT NULL,
    start_date    DATE NOT NULL,
    end_date      DATE,
    budget        DECIMAL(12,2) NOT NULL CHECK (budget > 0),
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- employee_projects table
CREATE TABLE employee_projects (
    emp_id      INT NOT NULL,
    project_id  INT NOT NULL,
    role        VARCHAR(20) NOT NULL DEFAULT 'Member',
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
