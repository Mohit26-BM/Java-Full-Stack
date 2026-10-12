-- Organization Database - insert sample data

USE org_db;

-- insert departments
INSERT INTO departments (dept_id, dept_name, location) VALUES
(1, 'Human Resources',          'Block A'),
(2, 'Finance',                  'Block A'),
(3, 'Information Technology',   'Block B'),
(4, 'Marketing',                'Block C'),
(5, 'Operations',               'Block C'),
(6, 'Research and Development', 'Block D');

-- insert employees
INSERT INTO employees (emp_id, first_name, last_name, email, phone, dept_id) VALUES
(1,  'Sara',   'Ahmed',    'sara.ahmed@example.com',    '555-0101', 1),
(2,  'Daniel', 'Brooks',   'daniel.brooks@example.com', NULL,       1),
(3,  'Fatima', 'Malik',    'fatima.malik@example.com',  '555-0103', 2),
(4,  'James',  'Carter',   'james.carter@example.com',  '555-0104', 2),
(5,  'Ali',    'Raza',     'ali.raza@example.com',      '555-0105', 3),
(6,  'Emily',  'Chen',     'emily.chen@example.com',    '555-0106', 3),
(7,  'Omar',   'Farooq',   'omar.farooq@example.com',   NULL,       3),
(8,  'Priya',  'Nair',     'priya.nair@example.com',    '555-0108', 4),
(9,  'Lucas',  'Silva',    'lucas.silva@example.com',   '555-0109', 4),
(10, 'Hina',   'Siddiqui', 'hina.siddiqui@example.com', '555-0110', 5);

-- insert projects
INSERT INTO projects (project_id, project_name, dept_id, start_date, end_date, budget) VALUES
(1, 'Payroll System Upgrade',     2, '2025-01-15', '2025-06-30',  50000.00),
(2, 'Recruitment Portal',         1, '2025-03-01', NULL,          30000.00),
(3, 'Cloud Migration',            3, '2025-02-10', NULL,         120000.00),
(4, 'Network Security Audit',     3, '2024-09-01', '2024-12-20',  45000.00),
(5, 'Spring Product Campaign',    4, '2025-04-01', '2025-05-31',  25000.00),
(6, 'Warehouse Automation Study', 5, '2025-07-01', NULL,          80000.00);

-- insert employee_projects
INSERT INTO employee_projects (emp_id, project_id, role) VALUES
(1, 2, 'Lead'),
(3, 1, 'Lead'),
(4, 1, 'Member'),
(5, 1, 'Member'),
(5, 3, 'Lead'),
(6, 3, 'Member'),
(7, 3, 'Member'),
(5, 4, 'Member'),
(6, 4, 'Lead'),
(8, 5, 'Lead'),
(9, 5, 'Member');

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
