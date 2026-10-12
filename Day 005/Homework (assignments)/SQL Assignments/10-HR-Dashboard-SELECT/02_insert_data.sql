-- HR Dashboard - insert sample data

USE hr_dashboard_db;

-- insert departments
INSERT INTO departments (dept_id, dept_name, location, annual_budget) VALUES
(1, 'Human Resources',        'Block A',  400000.00),
(2, 'Finance',                'Block A',  600000.00),
(3, 'Information Technology', 'Block B', 1000000.00),
(4, 'Sales',                  'Block C',  700000.00),
(5, 'Operations',             'Block D',  500000.00),
(6, 'Legal',                  'Block A',  300000.00);

-- insert employees
INSERT INTO employees (emp_id, first_name, last_name, email, dept_id, job_title, job_level, employment_type, salary, hire_date, status) VALUES
(1,  'Sara',    'Ahmed',    'sara.ahmed@example.com',    1, 'HR Manager',               'Manager', 'Full-Time',  95000.00, '2016-04-11', 'Active'),
(2,  'Daniel',  'Brooks',   'daniel.brooks@example.com', 1, 'HR Specialist',            'Mid',     'Full-Time',  62000.00, '2020-07-19', 'Active'),
(3,  'Amina',   'Yusuf',    'amina.yusuf@example.com',   1, 'Recruiter',                'Junior',  'Full-Time',  46000.00, '2023-03-06', 'Active'),
(4,  'Tom',     'Reed',     'tom.reed@example.com',      1, 'Payroll Officer',          'Mid',     'Part-Time',  38000.00, '2021-09-23', 'Active'),
(5,  'Nida',    'Khan',     'nida.khan@example.com',     1, 'Recruiter',                'Junior',  'Contract',   42000.00, '2024-02-12', 'Resigned'),
(6,  'Fatima',  'Malik',    'fatima.malik@example.com',  2, 'Finance Manager',          'Manager', 'Full-Time', 118000.00, '2015-02-02', 'Active'),
(7,  'James',   'Carter',   'james.carter@example.com',  2, 'Senior Accountant',        'Senior',  'Full-Time',  84000.00, '2017-06-18', 'Active'),
(8,  'Grace',   'Lee',      'grace.lee@example.com',     2, 'Financial Analyst',        'Mid',     'Full-Time',  71000.00, '2020-01-13', 'On Leave'),
(9,  'Bilal',   'Ahmed',    'bilal.ahmed@example.com',   2, 'Accounts Clerk',           'Junior',  'Full-Time',  45000.00, '2024-05-27', 'Active'),
(10, 'Mehwish', 'Riaz',     'mehwish.riaz@example.com',  2, 'Tax Specialist',           'Senior',  'Full-Time',  88000.00, '2018-11-04', 'Active'),
(11, 'Paul',    'Grant',    'paul.grant@example.com',    2, 'Accountant',               'Mid',     'Contract',   60000.00, '2022-08-08', 'Resigned'),
(12, 'Ali',     'Raza',     'ali.raza@example.com',      3, 'IT Manager',               'Manager', 'Full-Time', 132000.00, '2014-08-24', 'Active'),
(13, 'Emily',   'Chen',     'emily.chen@example.com',    3, 'Senior Software Engineer', 'Senior',  'Full-Time', 108000.00, '2018-03-11', 'Active'),
(14, 'Omar',    'Farooq',   'omar.farooq@example.com',   3, 'Software Engineer',        'Mid',     'Full-Time',  86000.00, '2021-10-04', 'Active'),
(15, 'Ayesha',  'Siddiq',   'ayesha.siddiq@example.com', 3, 'Database Administrator',   'Senior',  'Full-Time',  99000.00, '2017-01-15', 'Active'),
(16, 'Jason',   'Miller',   'jason.miller@example.com',  3, 'Network Engineer',         'Mid',     'Full-Time',  79000.00, '2020-08-31', 'Active'),
(17, 'Zara',    'Hussain',  'zara.hussain@example.com',  3, 'QA Engineer',              'Junior',  'Full-Time',  58000.00, '2023-02-14', 'Active'),
(18, 'Hannah',  'Moore',    'hannah.moore@example.com',  3, 'Data Analyst',             'Mid',     'Contract',   72000.00, '2022-06-26', 'Active'),
(19, 'Lucas',   'Silva',    'lucas.silva@example.com',   3, 'Junior Developer',         'Junior',  'Full-Time',  52000.00, '2024-09-02', 'On Leave'),
(20, 'Kevin',   'Wright',   'kevin.wright@example.com',  3, 'System Administrator',     'Mid',     'Full-Time',  74000.00, '2019-05-20', 'Resigned'),
(21, 'Priya',   'Nair',     'priya.nair@example.com',    4, 'Sales Manager',            'Manager', 'Full-Time', 105000.00, '2016-10-03', 'Active'),
(22, 'Ahmed',   'Khan',     'ahmed.khan@example.com',    4, 'Sales Executive',          'Mid',     'Full-Time',  57000.00, '2021-04-25', 'Active'),
(23, 'Olivia',  'Turner',   'olivia.turner@example.com', 4, 'Sales Executive',          'Mid',     'Full-Time',  59000.00, '2020-01-18', 'Active'),
(24, 'Nathan',  'Price',    'nathan.price@example.com',  4, 'Sales Representative',     'Junior',  'Part-Time',  34000.00, '2024-09-09', 'Active'),
(25, 'Hira',    'Aslam',    'hira.aslam@example.com',    4, 'Sales Representative',     'Junior',  'Full-Time',  44000.00, '2023-11-21', 'Active'),
(26, 'Adam',    'Fisher',   'adam.fisher@example.com',   4, 'Account Manager',          'Senior',  'Full-Time',  82000.00, '2018-05-07', 'Active'),
(27, 'Hina',    'Siddiqui', 'hina.siddiqui@example.com', 5, 'Operations Manager',       'Manager', 'Full-Time',  98000.00, '2015-10-13', 'Active'),
(28, 'Michael', 'Ross',     'michael.ross@example.com',  5, 'Logistics Coordinator',    'Mid',     'Full-Time',  61000.00, '2019-08-19', 'Active'),
(29, 'Waqas',   'Ahmed',    'waqas.ahmed@example.com',   5, 'Warehouse Supervisor',     'Senior',  'Full-Time',  66000.00, '2017-01-29', 'Active'),
(30, 'Ben',     'Carter',   'ben.carter@example.com',    5, 'Warehouse Associate',      'Junior',  'Part-Time',  32000.00, '2025-03-17', 'Active');

-- row counts
SELECT COUNT(*) AS department_rows FROM departments;
SELECT COUNT(*) AS employee_rows   FROM employees;

-- show all data
SELECT * FROM departments;
SELECT * FROM employees;
