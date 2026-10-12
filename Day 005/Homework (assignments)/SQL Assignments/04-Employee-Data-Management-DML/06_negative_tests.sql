-- Employee Data Management - negative tests (each statement should fail)

USE employee_dml_db;

-- t1 unique: duplicate email (error 1062)
INSERT INTO employees (first_name, last_name, email, dept_id, job_title, salary, hire_date)
VALUES ('Sara', 'Ali', 'sara.ahmed@example.com', 1, 'Recruiter', 52000.00, '2025-01-06');

-- t2 not null: missing last name (error 1048)
INSERT INTO employees (first_name, last_name, email, dept_id, job_title, salary, hire_date)
VALUES ('Zain', NULL, 'zain.k@example.com', 1, 'Recruiter', 52000.00, '2025-01-06');

-- t3 foreign key: department 99 does not exist (error 1452)
INSERT INTO employees (first_name, last_name, email, dept_id, job_title, salary, hire_date)
VALUES ('Adam', 'Stone', 'adam.stone@example.com', 99, 'Recruiter', 52000.00, '2025-01-06');

-- t4 check: negative salary update (error 3819)
UPDATE employees SET salary = -100.00 WHERE emp_id = 1;

-- t5 enum: status value not allowed (error 1265)
UPDATE employees SET status = 'Retired' WHERE emp_id = 2;

-- t6 delete blocked by foreign key: inactive employee still holds an asset (error 1451)
DELETE FROM employees WHERE emp_id = 24;

-- t7 foreign key: assign asset to employee 999 (error 1452)
UPDATE assets SET emp_id = 999 WHERE asset_id = 10;

-- row counts after tests
SELECT COUNT(*) AS department_rows FROM departments;
SELECT COUNT(*) AS employee_rows   FROM employees;
SELECT COUNT(*) AS asset_rows      FROM assets;
