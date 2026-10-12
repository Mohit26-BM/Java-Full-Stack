-- Capstone HR Management - negative tests (each statement should fail)

USE hr_management_db;

-- t1 unique: duplicate email (error 1062)
INSERT INTO employees (first_name, last_name, email, hire_date, job_id, dept_id, salary)
VALUES ('Sara', 'Ali', 'sara.ahmed@example.com', '2025-10-06', 2, 1, 50000.00);

-- t2 not null: missing last name (error 1048)
INSERT INTO employees (first_name, last_name, email, hire_date, job_id, dept_id, salary)
VALUES ('Zain', NULL, 'zain.k@example.com', '2025-10-06', 2, 1, 50000.00);

-- t3 foreign key: job 99 does not exist (error 1452)
INSERT INTO employees (first_name, last_name, email, hire_date, job_id, dept_id, salary)
VALUES ('Adam', 'Stone', 'adam.stone@example.com', '2025-10-06', 99, 1, 50000.00);

-- t4 foreign key on manager: employee 999 does not exist (error 1452)
UPDATE employees SET manager_id = 999 WHERE emp_id = 2;

-- t5 check: zero salary (error 3819)
UPDATE employees SET salary = 0 WHERE emp_id = 2;

-- t6 enum: status not allowed (error 1265)
UPDATE employees SET status = 'Retired' WHERE emp_id = 2;

-- t7 delete blocked: terminated employee still has leave history (error 1451)
DELETE FROM employees WHERE emp_id = 25;

-- t8 delete blocked: department has employees (error 1451)
DELETE FROM departments WHERE dept_id = 3;

-- t9 delete blocked: job is in use (error 1451)
DELETE FROM jobs WHERE job_id = 6;

-- row counts after tests
SELECT COUNT(*) AS department_rows FROM departments;
SELECT COUNT(*) AS job_rows        FROM jobs;
SELECT COUNT(*) AS employee_rows   FROM employees;
