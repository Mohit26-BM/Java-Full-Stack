-- Employee Reporting - negative tests (each statement should fail)

USE employee_report_db;

-- t1 unique: duplicate email (error 1062)
INSERT INTO employees (first_name, last_name, email, dept_id, job_title, salary, joining_date)
VALUES ('Sara', 'Ali', 'sara.ahmed@example.com', 1, 'Recruiter', 50000.00, '2025-06-02');

-- t2 not null: missing job title (error 1048)
INSERT INTO employees (first_name, last_name, email, dept_id, job_title, salary, joining_date)
VALUES ('Zain', 'Khan', 'zain.khan@example.com', 1, NULL, 50000.00, '2025-06-02');

-- t3 foreign key: department 99 does not exist (error 1452)
INSERT INTO employees (first_name, last_name, email, dept_id, job_title, salary, joining_date)
VALUES ('Adam', 'Stone', 'adam.stone@example.com', 99, 'Recruiter', 50000.00, '2025-06-02');

-- t4 check: zero salary (error 3819)
INSERT INTO employees (first_name, last_name, email, dept_id, job_title, salary, joining_date)
VALUES ('Mia', 'Hart', 'mia.hart@example.com', 1, 'Recruiter', 0.00, '2025-06-02');

-- t5 delete blocked by foreign key: department has employees (error 1451)
DELETE FROM departments WHERE dept_id = 3;

-- row counts after tests
SELECT COUNT(*) AS department_rows FROM departments;
SELECT COUNT(*) AS employee_rows   FROM employees;
