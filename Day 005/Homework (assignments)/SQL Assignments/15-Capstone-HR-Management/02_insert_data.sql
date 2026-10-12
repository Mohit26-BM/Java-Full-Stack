-- Capstone HR Management - insert sample data (DML)

USE hr_management_db;

-- insert departments
INSERT INTO departments (dept_id, dept_name, location, annual_budget) VALUES
(1, 'Human Resources', 'Block A',  450000.00),
(2, 'Finance',         'Block A',  600000.00),
(3, 'Engineering',     'Block B', 1200000.00),
(4, 'Sales',           'Block C',  700000.00),
(5, 'Operations',      'Block D',  500000.00),
(6, 'Legal',           'Block A',  300000.00);

-- insert jobs
INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES
(1,  'HR Manager',          85000.00, 120000.00),
(2,  'HR Officer',          45000.00,  70000.00),
(3,  'Finance Manager',    100000.00, 140000.00),
(4,  'Accountant',          55000.00,  85000.00),
(5,  'Engineering Manager',120000.00, 160000.00),
(6,  'Software Engineer',   70000.00, 120000.00),
(7,  'QA Engineer',         55000.00,  85000.00),
(8,  'Sales Manager',       90000.00, 130000.00),
(9,  'Sales Executive',     40000.00,  70000.00),
(10, 'Operations Manager',  85000.00, 120000.00),
(11, 'Operations Officer',  40000.00,  65000.00);

-- insert employees (managers first)
INSERT INTO employees (emp_id, first_name, last_name, email, phone, hire_date, job_id, dept_id, manager_id, salary, status) VALUES
(1,  'Sara',    'Ahmed',    'sara.ahmed@example.com',    '555-7001', '2016-04-11', 1,  1, NULL,  98000.00, 'Active'),
(4,  'Fatima',  'Malik',    'fatima.malik@example.com',  '555-7004', '2015-02-02', 3,  2, NULL, 125000.00, 'Active'),
(8,  'Ali',     'Raza',     'ali.raza@example.com',      '555-7008', '2014-08-24', 5,  3, NULL, 145000.00, 'Active'),
(15, 'Priya',   'Nair',     'priya.nair@example.com',    '555-7015', '2016-10-03', 8,  4, NULL, 112000.00, 'Active'),
(21, 'Hina',    'Siddiqui', 'hina.siddiqui@example.com', '555-7021', '2015-10-13', 10, 5, NULL,  99000.00, 'Active'),
(2,  'Daniel',  'Brooks',   'daniel.brooks@example.com', '555-7002', '2020-07-19', 2,  1, 1,     58000.00, 'Active'),
(3,  'Amina',   'Yusuf',    'amina.yusuf@example.com',   NULL,       '2023-03-06', 2,  1, 1,     47000.00, 'Active'),
(5,  'James',   'Carter',   'james.carter@example.com',  '555-7005', '2017-06-18', 4,  2, 4,     78000.00, 'Active'),
(6,  'Grace',   'Lee',      'grace.lee@example.com',     '555-7006', '2020-01-13', 4,  2, 4,     66000.00, 'On Leave'),
(7,  'Bilal',   'Ahmed',    'bilal.ahmed@example.com',   '555-7007', '2024-05-27', 4,  2, 4,     52000.00, 'Active'),
(9,  'Emily',   'Chen',     'emily.chen@example.com',    '555-7009', '2018-03-11', 6,  3, 8,    110000.00, 'Active'),
(10, 'Omar',    'Farooq',   'omar.farooq@example.com',   '555-7010', '2021-10-04', 6,  3, 8,     92000.00, 'Active'),
(11, 'Hannah',  'Moore',    'hannah.moore@example.com',  NULL,       '2022-06-26', 6,  3, 9,     84000.00, 'Active'),
(12, 'Lucas',   'Silva',    'lucas.silva@example.com',   '555-7012', '2024-09-02', 6,  3, 9,     72000.00, 'Active'),
(13, 'Zara',    'Hussain',  'zara.hussain@example.com',  '555-7013', '2023-02-14', 7,  3, 8,     64000.00, 'Active'),
(14, 'Kevin',   'Wright',   'kevin.wright@example.com',  '555-7014', '2019-05-20', 7,  3, 8,     76000.00, 'Active'),
(16, 'Ahmed',   'Khan',     'ahmed.khan@example.com',    '555-7016', '2021-04-25', 9,  4, 15,    56000.00, 'Active'),
(17, 'Olivia',  'Turner',   'olivia.turner@example.com', '555-7017', '2020-01-18', 9,  4, 15,    61000.00, 'Active'),
(18, 'Nathan',  'Price',    'nathan.price@example.com',  NULL,       '2024-09-09', 9,  4, 15,    42000.00, 'Active'),
(19, 'Hira',    'Aslam',    'hira.aslam@example.com',    '555-7019', '2023-11-21', 9,  4, 15,    45000.00, 'Active'),
(20, 'Adam',    'Fisher',   'adam.fisher@example.com',   '555-7020', '2018-05-07', 9,  4, 15,    74000.00, 'Active'),
(22, 'Michael', 'Ross',     'michael.ross@example.com',  '555-7022', '2019-08-19', 11, 5, 21,    58000.00, 'Active'),
(23, 'Waqas',   'Ahmed',    'waqas.ahmed@example.com',   '555-7023', '2017-01-29', 11, 5, 21,    62000.00, 'Active'),
(24, 'Ben',     'Carter',   'ben.carter@example.com',    '555-7024', '2025-03-17', 11, 5, 21,    41000.00, 'Active'),
(25, 'Tom',     'Reed',     'tom.reed@example.com',      '555-7025', '2018-09-23', 11, 5, 21,    48000.00, 'Active');

-- insert salary history
INSERT INTO salary_history (history_id, emp_id, old_salary, new_salary, change_date, change_reason) VALUES
(1, 9,  100000.00, 110000.00, '2024-04-01', 'Annual Raise'),
(2, 5,   72000.00,  78000.00, '2024-04-01', 'Annual Raise'),
(3, 15, 105000.00, 112000.00, '2024-07-01', 'Promotion'),
(4, 23,  58000.00,  62000.00, '2024-10-01', 'Annual Raise'),
(5, 10,  85000.00,  92000.00, '2025-01-01', 'Market Adjustment');

-- insert leave requests
INSERT INTO leave_requests (leave_id, emp_id, leave_type, start_date, end_date, status) VALUES
(1,  2,  'Annual', '2025-02-10', '2025-02-14', 'Approved'),
(2,  5,  'Sick',   '2025-03-03', '2025-03-04', 'Approved'),
(3,  9,  'Annual', '2025-04-14', '2025-04-25', 'Approved'),
(4,  6,  'Unpaid', '2025-09-01', '2025-11-30', 'Approved'),
(5,  10, 'Annual', '2025-05-19', '2025-05-23', 'Approved'),
(6,  16, 'Sick',   '2025-06-02', '2025-06-03', 'Approved'),
(7,  17, 'Annual', '2025-03-10', '2025-03-12', 'Rejected'),
(8,  13, 'Annual', '2025-06-16', '2025-06-20', 'Rejected'),
(9,  22, 'Annual', '2025-07-07', '2025-07-11', 'Approved'),
(10, 12, 'Sick',   '2025-08-18', '2025-08-19', 'Approved'),
(11, 3,  'Annual', '2025-10-20', '2025-10-24', 'Pending'),
(12, 14, 'Annual', '2025-11-03', '2025-11-07', 'Pending'),
(13, 19, 'Annual', '2025-10-13', '2025-10-15', 'Pending'),
(14, 23, 'Sick',   '2025-09-22', '2025-09-23', 'Approved'),
(15, 25, 'Annual', '2025-08-04', '2025-08-08', 'Approved'),
(16, 20, 'Annual', '2025-09-15', '2025-09-17', 'Rejected');

-- row counts
SELECT COUNT(*) AS department_rows FROM departments;
SELECT COUNT(*) AS job_rows        FROM jobs;
SELECT COUNT(*) AS employee_rows   FROM employees;
SELECT COUNT(*) AS history_rows    FROM salary_history;
SELECT COUNT(*) AS leave_rows      FROM leave_requests;

-- show all data
SELECT * FROM departments;
SELECT * FROM jobs;
SELECT * FROM employees;
SELECT * FROM salary_history;
SELECT * FROM leave_requests;
