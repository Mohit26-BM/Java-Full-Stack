-- Multi-Table Queries - negative tests (each statement should fail)

USE multi_table_db;

-- t1 unique: duplicate email (error 1062)
INSERT INTO employees (first_name, last_name, email, job_title, salary, dept_id, manager_id)
VALUES ('Sara', 'Ali', 'sara.ahmed@example.com', 'Recruiter', 50000.00, 1, 1);

-- t2 not null: missing project name (error 1048)
INSERT INTO projects (project_name, dept_id, start_date, budget)
VALUES (NULL, 3, '2025-10-01', 20000.00);

-- t3 foreign key: department 99 does not exist (error 1452)
INSERT INTO employees (first_name, last_name, email, job_title, salary, dept_id, manager_id)
VALUES ('Adam', 'Stone', 'adam.stone@example.com', 'Recruiter', 50000.00, 99, 1);

-- t4 foreign key on self join: manager 99 does not exist (error 1452)
INSERT INTO employees (first_name, last_name, email, job_title, salary, dept_id, manager_id)
VALUES ('Mia', 'Hart', 'mia.hart@example.com', 'Recruiter', 50000.00, 1, 99);

-- t5 check: zero salary (error 3819)
UPDATE employees SET salary = 0 WHERE emp_id = 5;

-- t6 primary key: same employee on same project twice (error 1062)
INSERT INTO employee_projects (emp_id, project_id, role, hours_per_week)
VALUES (8, 3, 'Member', 5);

-- t7 delete blocked by foreign key: department has employees and projects (error 1451)
DELETE FROM departments WHERE dept_id = 3;

-- t8 delete blocked by foreign key: employee is a manager and works on projects (error 1451)
DELETE FROM employees WHERE emp_id = 8;

-- row counts after tests
SELECT COUNT(*) AS department_rows       FROM departments;
SELECT COUNT(*) AS employee_rows         FROM employees;
SELECT COUNT(*) AS project_rows          FROM projects;
SELECT COUNT(*) AS employee_project_rows FROM employee_projects;
