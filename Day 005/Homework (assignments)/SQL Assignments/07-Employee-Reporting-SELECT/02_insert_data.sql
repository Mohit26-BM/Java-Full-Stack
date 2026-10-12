-- Employee Reporting - insert sample data

USE employee_report_db;

-- insert departments
INSERT INTO departments (dept_id, dept_name, location) VALUES
(1, 'Human Resources',        'Block A'),
(2, 'Finance',                'Block A'),
(3, 'Information Technology', 'Block B'),
(4, 'Sales',                  'Block C'),
(5, 'Marketing',              'Block C'),
(6, 'Research',               'Block D');

-- insert employees
INSERT INTO employees (emp_id, first_name, last_name, email, phone, dept_id, job_title, salary, joining_date) VALUES
(1,  'Sara',    'Ahmed',   'sara.ahmed@example.com',    '555-5001', 1, 'HR Manager',              92000.00, '2016-04-11'),
(2,  'Daniel',  'Brooks',  'daniel.brooks@example.com', '555-5002', 1, 'Recruiter',               52000.00, '2021-07-19'),
(3,  'Amina',   'Yusuf',   'amina.yusuf@example.com',   NULL,       1, 'HR Assistant',            41000.00, '2023-03-06'),
(4,  'Tom',     'Reed',    'tom.reed@example.com',      '555-5004', 1, 'Payroll Officer',         58000.00, '2019-09-23'),
(5,  'Fatima',  'Malik',   'fatima.malik@example.com',  '555-5005', 2, 'Finance Manager',        115000.00, '2015-02-02'),
(6,  'James',   'Carter',  'james.carter@example.com',  '555-5006', 2, 'Accountant',              67000.00, '2018-06-18'),
(7,  'Grace',   'Lee',     'grace.lee@example.com',     '555-5007', 2, 'Financial Analyst',       74000.00, '2020-01-13'),
(8,  'Bilal',   'Ahmed',   'bilal.ahmed@example.com',   '555-5008', 2, 'Accounts Clerk',          44000.00, '2024-05-27'),
(9,  'Mehwish', 'Riaz',    'mehwish.riaz@example.com',  NULL,       2, 'Tax Specialist',          79000.00, '2019-11-04'),
(10, 'Ali',     'Raza',    'ali.raza@example.com',      '555-5010', 3, 'IT Manager',             128000.00, '2015-08-24'),
(11, 'Emily',   'Chen',    'emily.chen@example.com',    '555-5011', 3, 'Software Engineer',       98000.00, '2019-03-11'),
(12, 'Omar',    'Farooq',  'omar.farooq@example.com',   '555-5012', 3, 'Software Engineer',       89000.00, '2021-10-04'),
(13, 'Ayesha',  'Siddiq',  'ayesha.siddiq@example.com', '555-5013', 3, 'Database Administrator',  94000.00, '2018-01-15'),
(14, 'Jason',   'Miller',  'jason.miller@example.com',  '555-5014', 3, 'Network Engineer',        82000.00, '2020-08-31'),
(15, 'Zara',    'Hussain', 'zara.hussain@example.com',  '555-5015', 3, 'QA Engineer',             69000.00, '2022-02-14'),
(16, 'Hannah',  'Moore',   'hannah.moore@example.com',  NULL,       3, 'Data Analyst',            76000.00, '2023-06-26'),
(17, 'Priya',   'Nair',    'priya.nair@example.com',    '555-5017', 4, 'Sales Manager',          104000.00, '2016-10-03'),
(18, 'Ahmed',   'Khan',    'ahmed.khan@example.com',    '555-5018', 4, 'Sales Executive',         56000.00, '2022-04-25'),
(19, 'Olivia',  'Turner',  'olivia.turner@example.com', '555-5019', 4, 'Sales Executive',         59000.00, '2021-01-18'),
(20, 'Nathan',  'Price',   'nathan.price@example.com',  '555-5020', 4, 'Sales Representative',    47000.00, '2024-09-09'),
(21, 'Hira',    'Aslam',   'hira.aslam@example.com',    '555-5021', 4, 'Sales Representative',    48000.00, '2022-11-21'),
(22, 'Laura',   'Bennett', 'laura.bennett@example.com', '555-5022', 5, 'Marketing Manager',       97000.00, '2017-05-08'),
(23, 'Lucas',   'Silva',   'lucas.silva@example.com',   '555-5023', 5, 'Content Strategist',      63000.00, '2022-07-11'),
(24, 'Sophie',  'Clark',   'sophie.clark@example.com',  '555-5024', 5, 'SEO Specialist',          61000.00, '2025-02-03');

-- row counts
SELECT COUNT(*) AS department_rows FROM departments;
SELECT COUNT(*) AS employee_rows   FROM employees;

-- show all data
SELECT * FROM departments;
SELECT * FROM employees;
