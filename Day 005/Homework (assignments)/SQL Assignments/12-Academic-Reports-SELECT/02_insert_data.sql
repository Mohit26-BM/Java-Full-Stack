-- Academic Reports - insert sample data

USE academic_db;

-- insert departments
INSERT INTO departments (dept_id, dept_name) VALUES
(1, 'Computer Science'),
(2, 'Business'),
(3, 'Mathematics'),
(4, 'Physics');

-- insert students
INSERT INTO students (student_id, roll_no, first_name, last_name, email, dept_id, intake_year) VALUES
(1,  'CS-23-001',  'Hamza',  'Iqbal',   'hamza.iqbal@uni.example.edu',   1, 2023),
(2,  'CS-23-002',  'Olivia', 'Turner',  'olivia.turner@uni.example.edu', 1, 2023),
(3,  'CS-23-003',  'Zainab', 'Hussain', 'zainab.hussain@uni.example.edu', 1, 2023),
(4,  'CS-24-001',  'Ethan',  'Walker',  'ethan.walker@uni.example.edu',  1, 2024),
(5,  'CS-24-002',  'Mariam', 'Shah',    'mariam.shah@uni.example.edu',   1, 2024),
(6,  'CS-24-003',  'Noah',   'Kim',     'noah.kim@uni.example.edu',      1, 2024),
(7,  'BUS-23-001', 'Ayesha', 'Butt',    'ayesha.butt@uni.example.edu',   2, 2023),
(8,  'BUS-23-002', 'Leo',    'Martins', 'leo.martins@uni.example.edu',   2, 2023),
(9,  'BUS-24-001', 'Sana',   'Mirza',   'sana.mirza@uni.example.edu',    2, 2024),
(10, 'BUS-24-002', 'Ryan',   'Hughes',  'ryan.hughes@uni.example.edu',   2, 2024),
(11, 'BUS-24-003', 'Hira',   'Aslam',   'hira.aslam@uni.example.edu',    2, 2024),
(12, 'MTH-23-001', 'Daniel', 'Park',    'daniel.park@uni.example.edu',   3, 2023),
(13, 'MTH-23-002', 'Fatima', 'Noor',    'fatima.noor@uni.example.edu',   3, 2023),
(14, 'MTH-24-001', 'Lucas',  'Gray',    'lucas.gray@uni.example.edu',    3, 2024),
(15, 'CS-25-001',  'Amna',   'Javed',   'amna.javed@uni.example.edu',    1, 2025);

-- insert courses
INSERT INTO courses (course_id, course_code, course_name, dept_id, credits, capacity) VALUES
(1, 'CS101',  'Programming Fundamentals',  1, 3, 40),
(2, 'CS201',  'Data Structures',           1, 4, 30),
(3, 'CS301',  'Database Systems',          1, 3, 30),
(4, 'BUS101', 'Principles of Management',  2, 3, 40),
(5, 'BUS205', 'Financial Accounting',      2, 3, 35),
(6, 'MTH101', 'Calculus I',                3, 4, 50),
(7, 'MTH202', 'Linear Algebra',            3, 3, 30),
(8, 'PHY101', 'Physics I',                 4, 4, 40);

-- insert enrollments
INSERT INTO enrollments (student_id, course_id, semester, marks) VALUES
(1,  1, 'Fall 2024',   88.00),
(1,  6, 'Fall 2024',   76.00),
(1,  2, 'Spring 2025', 91.00),
(1,  3, 'Fall 2025',   NULL),
(2,  1, 'Fall 2024',   72.00),
(2,  6, 'Fall 2024',   64.00),
(2,  2, 'Spring 2025', 68.00),
(2,  3, 'Fall 2025',   NULL),
(3,  1, 'Fall 2024',   95.00),
(3,  6, 'Fall 2024',   89.00),
(3,  2, 'Spring 2025', 93.00),
(3,  3, 'Fall 2025',   NULL),
(4,  1, 'Fall 2024',   58.00),
(4,  6, 'Fall 2024',   45.00),
(4,  2, 'Spring 2025', 52.00),
(5,  1, 'Fall 2024',   81.00),
(5,  6, 'Fall 2024',   77.00),
(5,  2, 'Spring 2025', 74.00),
(6,  1, 'Fall 2024',   47.00),
(6,  6, 'Fall 2024',   38.00),
(7,  4, 'Fall 2024',   84.00),
(7,  6, 'Fall 2024',   70.00),
(7,  5, 'Spring 2025', 79.00),
(8,  4, 'Fall 2024',   66.00),
(8,  5, 'Spring 2025', 55.00),
(9,  4, 'Fall 2024',   73.00),
(9,  5, 'Spring 2025', 81.00),
(10, 4, 'Fall 2024',   49.00),
(10, 5, 'Spring 2025', 61.00),
(11, 4, 'Fall 2024',   90.00),
(11, 5, 'Fall 2025',   NULL),
(12, 6, 'Fall 2024',   92.00),
(12, 7, 'Spring 2025', 87.00),
(13, 6, 'Fall 2024',   85.00),
(13, 7, 'Spring 2025', 78.00),
(13, 1, 'Spring 2025', 80.00),
(14, 6, 'Fall 2024',   61.00),
(14, 7, 'Spring 2025', 44.00);

-- row counts
SELECT COUNT(*) AS department_rows FROM departments;
SELECT COUNT(*) AS student_rows    FROM students;
SELECT COUNT(*) AS course_rows     FROM courses;
SELECT COUNT(*) AS enrollment_rows FROM enrollments;

-- show all data
SELECT * FROM departments;
SELECT * FROM students;
SELECT * FROM courses;
SELECT * FROM enrollments;
