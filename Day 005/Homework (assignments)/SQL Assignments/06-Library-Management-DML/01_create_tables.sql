-- Library Management - create database and tables

CREATE DATABASE library_db;
USE library_db;

-- books table
CREATE TABLE books (
    book_id         INT AUTO_INCREMENT PRIMARY KEY,
    isbn            VARCHAR(20)  NOT NULL UNIQUE,
    title           VARCHAR(150) NOT NULL,
    author          VARCHAR(100) NOT NULL,
    genre           VARCHAR(30)  NOT NULL,
    published_year  INT NOT NULL,
    status          ENUM('Available', 'Issued', 'Lost', 'Withdrawn') NOT NULL DEFAULT 'Available'
);

-- members table
CREATE TABLE members (
    member_id          INT AUTO_INCREMENT PRIMARY KEY,
    first_name         VARCHAR(50)  NOT NULL,
    last_name          VARCHAR(50)  NOT NULL,
    email              VARCHAR(100) NOT NULL UNIQUE,
    phone              VARCHAR(15)  UNIQUE,
    join_date          DATE NOT NULL,
    expiry_date        DATE NOT NULL,
    membership_status  ENUM('Active', 'Expired') NOT NULL DEFAULT 'Active'
);

-- issues table
CREATE TABLE issues (
    issue_id     INT AUTO_INCREMENT PRIMARY KEY,
    book_id      INT NOT NULL,
    member_id    INT NOT NULL,
    issue_date   DATE NOT NULL,
    due_date     DATE NOT NULL,
    return_date  DATE,
    CHECK (due_date > issue_date),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

-- show tables
SHOW TABLES;
DESCRIBE books;
DESCRIBE members;
DESCRIBE issues;
