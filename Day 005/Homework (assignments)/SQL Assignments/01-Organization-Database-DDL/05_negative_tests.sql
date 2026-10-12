-- Organization Database - negative tests (each statement should fail)

USE org_db;

-- t1 unique: duplicate department name (error 1062)
INSERT INTO departments (dept_name, location) VALUES ('Finance', 'Block E');

-- t2 unique: duplicate email (error 1062)
INSERT INTO employees (first_name, last_name, email, phone, dept_id, salary, joining_date)
VALUES ('Sara', 'Ali', 'sara.ahmed@example.com', '555-0201', 1, 50000.00, '2025-01-01');

-- t3 not null: missing last name (error 1048)
INSERT INTO employees (first_name, last_name, email, phone, dept_id, salary, joining_date)
VALUES ('Zara', NULL, 'zara.k@example.com', '555-0202', 1, 50000.00, '2025-01-01');

-- t4 foreign key: department 99 does not exist (error 1452)
INSERT INTO employees (first_name, last_name, email, phone, dept_id, salary, joining_date)
VALUES ('Adam', 'Stone', 'adam.stone@example.com', '555-0203', 99, 50000.00, '2025-01-01');

-- t5 check: negative budget (error 3819)
INSERT INTO projects (project_name, dept_id, start_date, end_date, budget)
VALUES ('Office Renovation', 5, '2025-08-01', NULL, -5000.00);

-- t6 primary key: same employee on same project twice (error 1062)
INSERT INTO employee_projects (emp_id, project_id, role) VALUES (5, 3, 'Member');

-- t7 delete blocked by foreign key: department has employees (error 1451)
DELETE FROM departments WHERE dept_id = 3;

-- t8 not null after alter: missing salary, run after 03 (error 1048)
INSERT INTO employees (first_name, last_name, email, phone, dept_id, salary, joining_date)
VALUES ('Mia', 'Hart', 'mia.hart@example.com', '555-0205', 4, NULL, '2025-01-01');

-- row counts after tests
SELECT COUNT(*) AS department_rows       FROM departments;
SELECT COUNT(*) AS employee_rows         FROM employees;
SELECT COUNT(*) AS project_rows          FROM projects;
SELECT COUNT(*) AS employee_project_rows FROM employee_projects;
