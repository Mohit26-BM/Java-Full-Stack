-- University Management - create database and tables

CREATE DATABASE university_db;
USE university_db;

-- students table
CREATE TABLE students (
    student_id     INT AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(50)  NOT NULL,
    last_name      VARCHAR(50)  NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    phone          VARCHAR(15)  UNIQUE,
    date_of_birth  DATE NOT NULL
);

-- courses table
CREATE TABLE courses (
    course_id    INT AUTO_INCREMENT PRIMARY KEY,
    course_code  VARCHAR(10)  NOT NULL UNIQUE,
    course_name  VARCHAR(100) NOT NULL,
    credits      INT NOT NULL CHECK (credits > 0)
);

-- enrollments table
CREATE TABLE enrollments (
    student_id       INT NOT NULL,
    course_id        INT NOT NULL,
    enrollment_date  DATE NOT NULL,
    grade            VARCHAR(2),
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- show tables
SHOW TABLES;
DESCRIBE students;
DESCRIBE courses;
DESCRIBE enrollments;
