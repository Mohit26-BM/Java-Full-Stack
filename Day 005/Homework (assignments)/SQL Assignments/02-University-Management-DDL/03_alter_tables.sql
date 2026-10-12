-- University Management - add course duration and instructor information

USE university_db;

-- structure before alter
DESCRIBE courses;

-- instructors table
CREATE TABLE instructors (
    instructor_id  INT AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(50)  NOT NULL,
    last_name      VARCHAR(50)  NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    department     VARCHAR(50)  NOT NULL
);

-- insert instructors
INSERT INTO instructors (instructor_id, first_name, last_name, email, department) VALUES
(1, 'Robert',  'Hayes',   'robert.hayes@example.edu',   'Computer Science'),
(2, 'Sana',    'Mirza',   'sana.mirza@example.edu',     'Computer Science'),
(3, 'Michael', 'Grant',   'michael.grant@example.edu',  'Mathematics'),
(4, 'Laura',   'Bennett', 'laura.bennett@example.edu',  'English'),
(5, 'Imran',   'Sheikh',  'imran.sheikh@example.edu',   'Physics');

-- add new columns to courses
ALTER TABLE courses
    ADD COLUMN duration_weeks INT AFTER credits,
    ADD COLUMN instructor_id  INT AFTER duration_weeks,
    ADD FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id);

-- fill values for existing courses
UPDATE courses SET duration_weeks = 16, instructor_id = 1 WHERE course_id = 1;
UPDATE courses SET duration_weeks = 16, instructor_id = 1 WHERE course_id = 2;
UPDATE courses SET duration_weeks = 12, instructor_id = 2 WHERE course_id = 3;
UPDATE courses SET duration_weeks = 16, instructor_id = 3 WHERE course_id = 4;
UPDATE courses SET duration_weeks = 8,  instructor_id = 4 WHERE course_id = 5;
UPDATE courses SET duration_weeks = 16, instructor_id = 5 WHERE course_id = 6;
UPDATE courses SET duration_weeks = 12 WHERE course_id = 7;

-- check for missing durations
SELECT COUNT(*) AS missing_durations
FROM courses
WHERE duration_weeks IS NULL;

-- make duration not null
ALTER TABLE courses
    MODIFY COLUMN duration_weeks INT NOT NULL;

-- structure after alter
SHOW TABLES;
DESCRIBE instructors;
DESCRIBE courses;
SELECT * FROM instructors;
SELECT * FROM courses;
