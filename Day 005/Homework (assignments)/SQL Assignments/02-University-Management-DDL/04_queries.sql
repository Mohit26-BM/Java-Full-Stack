-- University Management - select queries

USE university_db;

-- q1: students with their courses and grades
SELECT s.first_name, s.last_name, c.course_code, c.course_name, e.grade
FROM enrollments e
JOIN students s ON e.student_id = s.student_id
JOIN courses c  ON e.course_id = c.course_id
ORDER BY s.last_name, c.course_code;

-- q2: number of students in each course
SELECT c.course_code, c.course_name, COUNT(e.student_id) AS student_count
FROM courses c
LEFT JOIN enrollments e ON c.course_id = e.course_id
GROUP BY c.course_id, c.course_code, c.course_name
ORDER BY student_count DESC, c.course_code;

-- q3: total credits taken by each student
SELECT s.first_name, s.last_name,
       COUNT(e.course_id) AS courses_taken,
       SUM(c.credits)     AS total_credits
FROM students s
JOIN enrollments e ON s.student_id = e.student_id
JOIN courses c     ON e.course_id = c.course_id
GROUP BY s.student_id, s.first_name, s.last_name
ORDER BY total_credits DESC, s.last_name;

-- q4: courses with instructor and duration
SELECT c.course_code, c.course_name, c.duration_weeks,
       i.first_name AS instructor_first_name,
       i.last_name  AS instructor_last_name
FROM courses c
JOIN instructors i ON c.instructor_id = i.instructor_id
ORDER BY c.course_code;

-- q5: courses and total teaching weeks per instructor
SELECT i.first_name, i.last_name, i.department,
       COUNT(c.course_id)    AS courses_taught,
       SUM(c.duration_weeks) AS total_weeks
FROM instructors i
JOIN courses c ON i.instructor_id = c.instructor_id
GROUP BY i.instructor_id, i.first_name, i.last_name, i.department
ORDER BY courses_taught DESC, i.last_name;

-- q6: enrollments with no grade yet
SELECT s.first_name, s.last_name, c.course_code, e.enrollment_date
FROM enrollments e
JOIN students s ON e.student_id = s.student_id
JOIN courses c  ON e.course_id = c.course_id
WHERE e.grade IS NULL
ORDER BY e.enrollment_date;

-- q7: students not enrolled in any course
SELECT s.student_id, s.first_name, s.last_name
FROM students s
LEFT JOIN enrollments e ON s.student_id = e.student_id
WHERE e.student_id IS NULL;

-- q8: courses with no instructor assigned
SELECT course_code, course_name, duration_weeks
FROM courses
WHERE instructor_id IS NULL;
