-- University Management - insert sample data

USE university_db;

-- insert students
INSERT INTO students (student_id, first_name, last_name, email, phone, date_of_birth) VALUES
(1, 'Hamza',   'Iqbal',   'hamza.iqbal@student.example.edu',   '555-1001', '2003-04-15'),
(2, 'Olivia',  'Turner',  'olivia.turner@student.example.edu', NULL,       '2004-01-22'),
(3, 'Zainab',  'Hussain', 'zainab.hussain@student.example.edu', '555-1003', '2003-09-03'),
(4, 'Ethan',   'Walker',  'ethan.walker@student.example.edu',  '555-1004', '2002-12-11'),
(5, 'Mariam',  'Shah',    'mariam.shah@student.example.edu',   '555-1005', '2004-06-30'),
(6, 'Noah',    'Kim',     'noah.kim@student.example.edu',      NULL,       '2003-02-18'),
(7, 'Ayesha',  'Butt',    'ayesha.butt@student.example.edu',   '555-1007', '2004-11-07'),
(8, 'Leo',     'Martins', 'leo.martins@student.example.edu',   '555-1008', '2003-07-25');

-- insert courses
INSERT INTO courses (course_id, course_code, course_name, credits) VALUES
(1, 'CS101', 'Introduction to Programming', 3),
(2, 'CS201', 'Data Structures',             4),
(3, 'DB301', 'Database Systems',            3),
(4, 'MA101', 'Calculus I',                  4),
(5, 'EN102', 'Academic Writing',            2),
(6, 'PH110', 'Physics I',                   4),
(7, 'CS450', 'Machine Learning',            3);

-- insert enrollments
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade) VALUES
(1, 1, '2025-01-20', 'A'),
(1, 3, '2025-01-20', 'B+'),
(1, 4, '2025-01-21', 'B'),
(2, 1, '2025-01-20', 'A-'),
(2, 5, '2025-01-22', NULL),
(3, 2, '2025-01-21', 'A'),
(3, 3, '2025-01-21', NULL),
(4, 4, '2025-01-23', 'C+'),
(4, 6, '2025-01-23', 'B-'),
(5, 1, '2025-01-20', 'B'),
(5, 5, '2025-01-22', 'A'),
(6, 2, '2025-01-24', NULL),
(6, 6, '2025-01-24', 'B+'),
(7, 3, '2025-01-25', 'A-'),
(7, 1, '2025-01-25', NULL);

-- row counts
SELECT COUNT(*) AS student_rows    FROM students;
SELECT COUNT(*) AS course_rows     FROM courses;
SELECT COUNT(*) AS enrollment_rows FROM enrollments;

-- show all data
SELECT * FROM students;
SELECT * FROM courses;
SELECT * FROM enrollments;
