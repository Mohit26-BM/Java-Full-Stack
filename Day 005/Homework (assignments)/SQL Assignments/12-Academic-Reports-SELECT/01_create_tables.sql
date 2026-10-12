-- Academic Reports - create database and tables

CREATE DATABASE academic_db;
USE academic_db;

-- departments table
CREATE TABLE departments (
    dept_id    INT AUTO_INCREMENT PRIMARY KEY,
    dept_name  VARCHAR(50) NOT NULL UNIQUE
);

-- students table
CREATE TABLE students (
    student_id   INT AUTO_INCREMENT PRIMARY KEY,
    roll_no      VARCHAR(15)  NOT NULL UNIQUE,
    first_name   VARCHAR(50)  NOT NULL,
    last_name    VARCHAR(50)  NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    dept_id      INT NOT NULL,
    intake_year  INT NOT NULL,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- courses table
CREATE TABLE courses (
    course_id    INT AUTO_INCREMENT PRIMARY KEY,
    course_code  VARCHAR(10)  NOT NULL UNIQUE,
    course_name  VARCHAR(100) NOT NULL,
    dept_id      INT NOT NULL,
    credits      INT NOT NULL,
    capacity     INT NOT NULL,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- enrollments table
CREATE TABLE enrollments (
    student_id  INT NOT NULL,
    course_id   INT NOT NULL,
    semester    VARCHAR(20) NOT NULL,
    marks       DECIMAL(5,2) CHECK (marks BETWEEN 0 AND 100),
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- show tables
SHOW TABLES;
DESCRIBE departments;
DESCRIBE students;
DESCRIBE courses;
DESCRIBE enrollments;
