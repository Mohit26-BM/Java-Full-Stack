-- Academic Reports - negative tests (each statement should fail)

USE academic_db;

-- t1 unique: duplicate roll number (error 1062)
INSERT INTO students (roll_no, first_name, last_name, email, dept_id, intake_year)
VALUES ('CS-23-001', 'Hamid', 'Ali', 'hamid.ali@uni.example.edu', 1, 2025);

-- t2 not null: missing course name (error 1048)
INSERT INTO courses (course_code, course_name, dept_id, credits, capacity)
VALUES ('CS401', NULL, 1, 3, 30);

-- t3 foreign key: student 99 does not exist (error 1452)
INSERT INTO enrollments (student_id, course_id, semester, marks)
VALUES (99, 1, 'Fall 2025', NULL);

-- t4 check: marks above 100 (error 3819)
UPDATE enrollments SET marks = 105.00 WHERE student_id = 1 AND course_id = 1;

-- t5 primary key: same student in same course twice (error 1062)
INSERT INTO enrollments (student_id, course_id, semester, marks)
VALUES (1, 1, 'Fall 2025', NULL);

-- t6 delete blocked by foreign key: student has enrollments (error 1451)
DELETE FROM students WHERE student_id = 1;

-- row counts after tests
SELECT COUNT(*) AS student_rows    FROM students;
SELECT COUNT(*) AS enrollment_rows FROM enrollments;
