-- Multi-Table Queries - insert sample data

USE multi_table_db;

-- insert departments
INSERT INTO departments (dept_id, dept_name, location) VALUES
(1, 'Human Resources',        'Block A'),
(2, 'Finance',                'Block A'),
(3, 'Information Technology', 'Block B'),
(4, 'Marketing',              'Block C'),
(5, 'Legal',                  'Block D');

-- insert employees (managers first)
INSERT INTO employees (emp_id, first_name, last_name, email, job_title, salary, dept_id, manager_id) VALUES
(1,  'Sara',   'Ahmed',   'sara.ahmed@example.com',    'HR Manager',         95000.00, 1, NULL),
(2,  'Fatima', 'Malik',   'fatima.malik@example.com',  'Finance Manager',   115000.00, 2, NULL),
(3,  'Ali',    'Raza',    'ali.raza@example.com',      'IT Manager',        128000.00, 3, NULL),
(4,  'Laura',  'Bennett', 'laura.bennett@example.com', 'Marketing Manager',  97000.00, 4, NULL),
(5,  'Daniel', 'Brooks',  'daniel.brooks@example.com', 'Recruiter',          52000.00, 1, 1),
(6,  'James',  'Carter',  'james.carter@example.com',  'Accountant',         67000.00, 2, 2),
(7,  'Grace',  'Lee',     'grace.lee@example.com',     'Financial Analyst',  74000.00, 2, 2),
(8,  'Emily',  'Chen',    'emily.chen@example.com',    'Software Engineer',  98000.00, 3, 3),
(9,  'Omar',   'Farooq',  'omar.farooq@example.com',   'Software Engineer',  89000.00, 3, 3),
(10, 'Zara',   'Hussain', 'zara.hussain@example.com',  'QA Engineer',        69000.00, 3, 8),
(11, 'Hannah', 'Moore',   'hannah.moore@example.com',  'Data Analyst',       76000.00, 3, 3),
(12, 'Lucas',  'Silva',   'lucas.silva@example.com',   'Content Strategist', 63000.00, 4, 4),
(13, 'Sophie', 'Clark',   'sophie.clark@example.com',  'SEO Specialist',     61000.00, 4, 4),
(14, 'Noah',   'Kim',     'noah.kim@example.com',      'IT Consultant',      70000.00, NULL, NULL);

-- insert projects
INSERT INTO projects (project_id, project_name, dept_id, start_date, end_date, budget) VALUES
(1, 'Payroll System Upgrade',       2, '2025-01-15', '2025-06-30',  50000.00),
(2, 'Recruitment Portal',           1, '2025-03-01', NULL,          30000.00),
(3, 'Cloud Migration',              3, '2025-02-10', NULL,         120000.00),
(4, 'Customer Analytics Dashboard', 3, '2025-05-05', NULL,          60000.00),
(5, 'Brand Refresh Campaign',       4, '2025-04-01', '2025-08-31',  40000.00),
(6, 'Compliance Review',            5, '2025-09-01', NULL,          25000.00);

-- insert employee_projects
INSERT INTO employee_projects (emp_id, project_id, role, hours_per_week) VALUES
(2,  1, 'Lead',    5),
(6,  1, 'Member', 20),
(7,  1, 'Member', 15),
(8,  1, 'Member', 10),
(1,  2, 'Lead',    8),
(5,  2, 'Member', 20),
(11, 2, 'Member', 10),
(3,  3, 'Lead',   10),
(8,  3, 'Member', 25),
(9,  3, 'Member', 30),
(10, 3, 'Member', 20),
(9,  4, 'Lead',   10),
(11, 4, 'Member', 25),
(7,  4, 'Member', 10),
(4,  5, 'Lead',   10),
(12, 5, 'Member', 30),
(13, 5, 'Member', 30);

-- row counts
SELECT COUNT(*) AS department_rows       FROM departments;
SELECT COUNT(*) AS employee_rows         FROM employees;
SELECT COUNT(*) AS project_rows          FROM projects;
SELECT COUNT(*) AS employee_project_rows FROM employee_projects;

-- show all data
SELECT * FROM departments;
SELECT * FROM employees;
SELECT * FROM projects;
SELECT * FROM employee_projects;
