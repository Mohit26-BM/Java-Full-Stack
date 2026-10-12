-- Academic Reports - student performance and course enrollment statistics (pass mark is 50)

USE academic_db;

-- student performance

-- q1: every graded result with letter grade
SELECT s.roll_no, s.first_name, s.last_name, c.course_code, e.semester, e.marks,
       CASE
           WHEN e.marks >= 85 THEN 'A'
           WHEN e.marks >= 70 THEN 'B'
           WHEN e.marks >= 60 THEN 'C'
           WHEN e.marks >= 50 THEN 'D'
           ELSE 'F'
       END AS grade
FROM enrollments e
JOIN students s ON e.student_id = s.student_id
JOIN courses c  ON e.course_id = c.course_id
WHERE e.marks IS NOT NULL
ORDER BY s.roll_no, e.semester, c.course_code;

-- q2: average marks and courses completed per student
SELECT s.roll_no, s.first_name, s.last_name,
       COUNT(e.marks)          AS courses_completed,
       ROUND(AVG(e.marks), 2)  AS average_marks,
       MIN(e.marks)            AS lowest_mark,
       MAX(e.marks)            AS highest_mark
FROM students s
JOIN enrollments e ON s.student_id = e.student_id
WHERE e.marks IS NOT NULL
GROUP BY s.student_id, s.roll_no, s.first_name, s.last_name
ORDER BY average_marks DESC;

-- q3: top 3 students by average marks
SELECT s.first_name, s.last_name, ROUND(AVG(e.marks), 2) AS average_marks
FROM students s
JOIN enrollments e ON s.student_id = e.student_id
WHERE e.marks IS NOT NULL
GROUP BY s.student_id, s.first_name, s.last_name
ORDER BY average_marks DESC
LIMIT 3;

-- q4: failed results (marks below 50)
SELECT s.first_name, s.last_name, c.course_code, c.course_name, e.semester, e.marks
FROM enrollments e
JOIN students s ON e.student_id = s.student_id
JOIN courses c  ON e.course_id = c.course_id
WHERE e.marks < 50
ORDER BY e.marks;

-- q5: students on probation (average below 60)
SELECT s.roll_no, s.first_name, s.last_name, ROUND(AVG(e.marks), 2) AS average_marks
FROM students s
JOIN enrollments e ON s.student_id = e.student_id
WHERE e.marks IS NOT NULL
GROUP BY s.student_id, s.roll_no, s.first_name, s.last_name
HAVING AVG(e.marks) < 60
ORDER BY average_marks;

-- q6: average marks by student department
SELECT d.dept_name,
       COUNT(DISTINCT s.student_id) AS students,
       ROUND(AVG(e.marks), 2)       AS average_marks
FROM departments d
JOIN students s    ON d.dept_id = s.dept_id
JOIN enrollments e ON s.student_id = e.student_id
WHERE e.marks IS NOT NULL
GROUP BY d.dept_id, d.dept_name
ORDER BY average_marks DESC;

-- q7: average marks by intake year
SELECT s.intake_year, COUNT(DISTINCT s.student_id) AS students, ROUND(AVG(e.marks), 2) AS average_marks
FROM students s
JOIN enrollments e ON s.student_id = e.student_id
WHERE e.marks IS NOT NULL
GROUP BY s.intake_year
ORDER BY s.intake_year;

-- course enrollment statistics

-- q8: enrollments per course compared with capacity, including empty courses
SELECT c.course_code, c.course_name, c.capacity,
       COUNT(e.student_id)                         AS enrolled,
       ROUND(COUNT(e.student_id) / c.capacity * 100, 1) AS percent_full
FROM courses c
LEFT JOIN enrollments e ON c.course_id = e.course_id
GROUP BY c.course_id, c.course_code, c.course_name, c.capacity
ORDER BY enrolled DESC, c.course_code;

-- q9: marks summary per course
SELECT c.course_code, c.course_name,
       COUNT(e.marks)         AS graded,
       ROUND(AVG(e.marks), 2) AS average_marks,
       MIN(e.marks)           AS lowest_mark,
       MAX(e.marks)           AS highest_mark
FROM courses c
JOIN enrollments e ON c.course_id = e.course_id
WHERE e.marks IS NOT NULL
GROUP BY c.course_id, c.course_code, c.course_name
ORDER BY average_marks DESC;

-- q10: pass and fail count per course
SELECT c.course_code,
       SUM(CASE WHEN e.marks >= 50 THEN 1 ELSE 0 END) AS passed,
       SUM(CASE WHEN e.marks < 50  THEN 1 ELSE 0 END) AS failed
FROM courses c
JOIN enrollments e ON c.course_id = e.course_id
WHERE e.marks IS NOT NULL
GROUP BY c.course_id, c.course_code
ORDER BY failed DESC, c.course_code;

-- q11: enrollments per semester
SELECT semester, COUNT(*) AS enrollments, COUNT(marks) AS graded
FROM enrollments
GROUP BY semester
ORDER BY enrollments DESC;

-- q12: courses still in progress (no marks yet)
SELECT s.first_name, s.last_name, c.course_code, e.semester
FROM enrollments e
JOIN students s ON e.student_id = s.student_id
JOIN courses c  ON e.course_id = c.course_id
WHERE e.marks IS NULL
ORDER BY c.course_code, s.last_name;

-- q13: courses with no enrollments
SELECT c.course_code, c.course_name
FROM courses c
LEFT JOIN enrollments e ON c.course_id = e.course_id
WHERE e.student_id IS NULL;

-- q14: students with no enrollments
SELECT s.roll_no, s.first_name, s.last_name, s.intake_year
FROM students s
LEFT JOIN enrollments e ON s.student_id = e.student_id
WHERE e.course_id IS NULL;
