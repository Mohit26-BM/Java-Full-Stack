-- Employee Data Management - insert 50 employees and related data

USE employee_dml_db;

-- insert departments
INSERT INTO departments (dept_id, dept_name, location) VALUES
(1, 'Human Resources',        'Block A'),
(2, 'Finance',                'Block A'),
(3, 'Information Technology', 'Block B'),
(4, 'Sales',                  'Block C'),
(5, 'Operations',             'Block D'),
(6, 'Legal',                  'Block A');

-- insert employees
INSERT INTO employees (emp_id, first_name, last_name, email, dept_id, job_title, salary, hire_date, status) VALUES
(1,  'Sara',    'Ahmed',    'sara.ahmed@example.com',     1, 'HR Manager',             95000.00, '2015-03-10', 'Active'),
(2,  'Daniel',  'Brooks',   'daniel.brooks@example.com',  1, 'HR Specialist',          62000.00, '2019-06-17', 'Active'),
(3,  'Amina',   'Yusuf',    'amina.yusuf@example.com',    1, 'Recruiter',              55000.00, '2021-02-01', 'Active'),
(4,  'Mark',    'Ellis',    'mark.ellis@example.com',     1, 'Recruiter',              54000.00, '2022-08-15', 'Active'),
(5,  'Rabia',   'Anwar',    'rabia.anwar@example.com',    1, 'Payroll Officer',        58000.00, '2018-11-05', 'Active'),
(6,  'Chris',   'Doyle',    'chris.doyle@example.com',    1, 'Training Coordinator',   57000.00, '2020-04-20', 'Active'),
(7,  'Hassan',  'Ali',      'hassan.ali@example.com',     1, 'HR Assistant',           42000.00, '2017-09-12', 'Inactive'),
(8,  'Julia',   'Park',     'julia.park@example.com',     1, 'HR Assistant',           43000.00, '2023-01-09', 'Active'),
(9,  'Nida',    'Khan',     'nida.khan@example.com',      1, 'Benefits Analyst',       60000.00, '2019-10-28', 'Active'),
(10, 'Tom',     'Reed',     'tom.reed@example.com',       1, 'HR Specialist',          61000.00, '2016-05-23', 'Active'),
(11, 'Fatima',  'Malik',    'fatima.malik@example.com',   2, 'Finance Manager',       110000.00, '2014-07-01', 'Active'),
(12, 'James',   'Carter',   'james.carter@example.com',   2, 'Accountant',             68000.00, '2018-03-19', 'Active'),
(13, 'Bilal',   'Ahmed',    'bilal.ahmed@example.com',    2, 'Accountant',             66000.00, '2020-09-14', 'Active'),
(14, 'Grace',   'Lee',      'grace.lee@example.com',      2, 'Financial Analyst',      72000.00, '2019-01-07', 'Active'),
(15, 'Kamran',  'Javed',    'kamran.javed@example.com',   2, 'Accounts Clerk',         45000.00, '2016-02-22', 'Inactive'),
(16, 'Emma',    'Scott',    'emma.scott@example.com',     2, 'Financial Analyst',      74000.00, '2021-06-01', 'Active'),
(17, 'Sadia',   'Noor',     'sadia.noor@example.com',     2, 'Auditor',                70000.00, '2017-12-04', 'Active'),
(18, 'Ryan',    'Hughes',   'ryan.hughes@example.com',    2, 'Accounts Clerk',         46000.00, '2022-03-28', 'Active'),
(19, 'Mehwish', 'Riaz',     'mehwish.riaz@example.com',   2, 'Tax Specialist',         76000.00, '2018-08-13', 'Active'),
(20, 'Paul',    'Grant',    'paul.grant@example.com',     2, 'Accountant',             67000.00, '2023-05-15', 'Active'),
(21, 'Ali',     'Raza',     'ali.raza@example.com',       3, 'IT Manager',            125000.00, '2013-04-08', 'Active'),
(22, 'Emily',   'Chen',     'emily.chen@example.com',     3, 'Software Engineer',      98000.00, '2019-07-22', 'Active'),
(23, 'Omar',    'Farooq',   'omar.farooq@example.com',    3, 'Software Engineer',      92000.00, '2021-03-15', 'Active'),
(24, 'Kevin',   'Wright',   'kevin.wright@example.com',   3, 'System Administrator',   78000.00, '2017-10-02', 'Inactive'),
(25, 'Ayesha',  'Siddiq',   'ayesha.siddiq@example.com',  3, 'Database Administrator', 95000.00, '2018-02-26', 'Active'),
(26, 'Jason',   'Miller',   'jason.miller@example.com',   3, 'Network Engineer',       84000.00, '2020-11-09', 'Active'),
(27, 'Zara',    'Hussain',  'zara.hussain@example.com',   3, 'QA Engineer',            72000.00, '2022-01-17', 'Active'),
(28, 'Lucas',   'Silva',    'lucas.silva@example.com',    3, 'Software Engineer',      90000.00, '2021-09-06', 'Active'),
(29, 'Usman',   'Tariq',    'usman.tariq@example.com',    3, 'Help Desk Technician',   48000.00, '2016-06-13', 'Inactive'),
(30, 'Hannah',  'Moore',    'hannah.moore@example.com',   3, 'Data Analyst',           80000.00, '2023-02-20', 'Active'),
(31, 'Priya',   'Nair',     'priya.nair@example.com',     4, 'Sales Manager',         105000.00, '2015-09-01', 'Active'),
(32, 'Ahmed',   'Khan',     'ahmed.khan@example.com',     4, 'Sales Executive',        58000.00, '2019-04-15', 'Active'),
(33, 'Olivia',  'Turner',   'olivia.turner@example.com',  4, 'Sales Executive',        60000.00, '2020-02-03', 'Active'),
(34, 'Imran',   'Qureshi',  'imran.qureshi@example.com',  4, 'Account Manager',        75000.00, '2017-06-26', 'Active'),
(35, 'Sophie',  'Clark',    'sophie.clark@example.com',   4, 'Sales Executive',        57000.00, '2022-10-10', 'Active'),
(36, 'Faisal',  'Mehmood',  'faisal.mehmood@example.com', 4, 'Account Manager',        73000.00, '2016-11-21', 'Inactive'),
(37, 'Nathan',  'Price',    'nathan.price@example.com',   4, 'Sales Representative',   50000.00, '2023-07-03', 'Active'),
(38, 'Hira',    'Aslam',    'hira.aslam@example.com',     4, 'Sales Representative',   49000.00, '2021-12-13', 'Active'),
(39, 'Adam',    'Fisher',   'adam.fisher@example.com',    4, 'Business Development',   82000.00, '2018-05-07', 'Active'),
(40, 'Saima',   'Bano',     'saima.bano@example.com',     4, 'Sales Representative',   47000.00, '2017-03-20', 'Inactive'),
(41, 'Hina',    'Siddiqui', 'hina.siddiqui@example.com',  5, 'Operations Manager',    100000.00, '2014-10-13', 'Active'),
(42, 'Michael', 'Ross',     'michael.ross@example.com',   5, 'Logistics Coordinator',  62000.00, '2019-08-19', 'Active'),
(43, 'Waqas',   'Ahmed',    'waqas.ahmed@example.com',    5, 'Warehouse Supervisor',   58000.00, '2018-01-29', 'Active'),
(44, 'Laura',   'Bennett',  'laura.bennett@example.com',  5, 'Operations Analyst',     69000.00, '2020-06-22', 'Active'),
(45, 'Tariq',   'Mahmood',  'tariq.mahmood@example.com',  5, 'Inventory Controller',   55000.00, '2021-04-12', 'Active'),
(46, 'George',  'Hall',     'george.hall@example.com',    5, 'Logistics Coordinator',  61000.00, '2016-08-08', 'Inactive'),
(47, 'Sana',    'Mirza',    'sana.mirza@example.com',     5, 'Procurement Officer',    64000.00, '2019-12-02', 'Active'),
(48, 'Ben',     'Carter',   'ben.carter@example.com',     5, 'Warehouse Associate',    41000.00, '2022-05-16', 'Active'),
(49, 'Asif',    'Iqbal',    'asif.iqbal@example.com',     5, 'Warehouse Associate',    40000.00, '2015-07-27', 'Inactive'),
(50, 'Chloe',   'Adams',    'chloe.adams@example.com',    5, 'Procurement Officer',    63000.00, '2023-09-11', 'Active');

-- insert assets
INSERT INTO assets (asset_id, asset_tag, asset_type, emp_id) VALUES
(1,  'LT-1001', 'Laptop',       22),
(2,  'LT-1002', 'Laptop',       24),
(3,  'LT-1003', 'Laptop',       31),
(4,  'PH-2001', 'Mobile Phone', 36),
(5,  'PH-2002', 'Mobile Phone', 34),
(6,  'LT-1004', 'Laptop',       11),
(7,  'AC-3001', 'Access Card',  46),
(8,  'LT-1005', 'Laptop',       41),
(9,  'MN-4001', 'Monitor',      25),
(10, 'LT-1006', 'Laptop',       NULL);

-- row counts
SELECT COUNT(*) AS department_rows FROM departments;
SELECT COUNT(*) AS employee_rows   FROM employees;
SELECT COUNT(*) AS asset_rows      FROM assets;

-- employees by status
SELECT status, COUNT(*) AS employee_count
FROM employees
GROUP BY status;

-- show all data
SELECT * FROM departments;
SELECT * FROM employees;
SELECT * FROM assets;
