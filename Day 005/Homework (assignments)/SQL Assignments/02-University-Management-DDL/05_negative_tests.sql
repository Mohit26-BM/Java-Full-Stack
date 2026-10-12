-- University Management - negative tests (each statement should fail)

USE university_db;

-- t1 unique: duplicate student email (error 1062)
INSERT INTO students (first_name, last_name, email, phone, date_of_birth)
VALUES ('Hamid', 'Iqbal', 'hamza.iqbal@student.example.edu', '555-1101', '2003-05-10');

-- t2 unique: duplicate course code (error 1062)
INSERT INTO courses (course_code, course_name, credits, duration_weeks)
VALUES ('CS101', 'Programming Basics', 3, 16);

-- t3 not null: missing course name (error 1048)
INSERT INTO courses (course_code, course_name, credits, duration_weeks)
VALUES ('CS120', NULL, 3, 16);

-- t4 foreign key: student 99 does not exist (error 1452)
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade)
VALUES (99, 1, '2025-02-01', NULL);

-- t5 check: zero credits (error 3819)
INSERT INTO courses (course_code, course_name, credits, duration_weeks)
VALUES ('CS130', 'Ethics in Computing', 0, 8);

-- t6 primary key: same student in same course twice (error 1062)
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade)
VALUES (1, 1, '2025-02-01', NULL);

-- t7 delete blocked by foreign key: student has enrollments (error 1451)
DELETE FROM students WHERE student_id = 1;

-- t8 not null after alter: missing duration, run after 03 (error 1048)
INSERT INTO courses (course_code, course_name, credits, duration_weeks)
VALUES ('PH210', 'Physics II', 4, NULL);

-- t9 foreign key after alter: instructor 99 does not exist, run after 03 (error 1452)
INSERT INTO courses (course_code, course_name, credits, duration_weeks, instructor_id)
VALUES ('MA201', 'Calculus II', 4, 16, 99);

-- row counts after tests
SELECT COUNT(*) AS student_rows    FROM students;
SELECT COUNT(*) AS course_rows     FROM courses;
SELECT COUNT(*) AS enrollment_rows FROM enrollments;
SELECT COUNT(*) AS instructor_rows FROM instructors;
