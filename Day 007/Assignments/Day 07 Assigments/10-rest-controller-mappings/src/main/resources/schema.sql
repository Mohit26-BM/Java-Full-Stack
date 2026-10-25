-- =========================================================
-- Day 07 - Assignment 10: REST Controller and Mappings
-- Shared database Day_007_Assigments; this script only touches a10_ tables.
-- Run once before starting the application:
--   mysql -u root -p < schema.sql
-- =========================================================

CREATE DATABASE IF NOT EXISTS Day_007_Assigments;
USE Day_007_Assigments;

DROP TABLE IF EXISTS a10_greetings;

CREATE TABLE a10_greetings (
    greeting_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    message     VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO a10_greetings (name, message) VALUES
    ('Asha', 'Good morning'),
    ('Ravi', 'Happy coding');
