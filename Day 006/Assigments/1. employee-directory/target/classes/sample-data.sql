-- =========================================================
-- Employee Directory - Sample Data
-- Run after schema.sql:  mysql -u root -p < sample-data.sql
-- 5 departments, 15 employees
-- =========================================================

USE employee_directory;

INSERT INTO departments (department_name) VALUES
('Engineering'),
('Human Resources'),
('Finance'),
('Marketing'),
('Operations');

INSERT INTO employees
(employee_code, employee_name, email, phone, department_id, salary, joining_date)
VALUES
('EMP001', 'Ravi Kumar',     'ravi.kumar@company.com',     '9810000001', 1, 85000.00, '2021-04-12'),
('EMP002', 'Priya Sharma',   'priya.sharma@company.com',   '9810000002', 1, 92000.00, '2020-07-01'),
('EMP003', 'Amit Verma',     'amit.verma@company.com',     '9810000003', 1, 64000.00, '2023-01-16'),
('EMP004', 'Neha Iyer',      'neha.iyer@company.com',      '9810000004', 2, 58000.00, '2019-11-20'),
('EMP005', 'Karan Patel',    'karan.patel@company.com',    '9810000005', 2, 45000.00, '2024-02-05'),
('EMP006', 'Anjali Gupta',   'anjali.gupta@company.com',   '9810000006', 3, 98000.00, '2018-03-12'),
('EMP007', 'Rahul Singh',    'rahul.singh@company.com',    '9810000007', 3, 61000.00, '2022-08-23'),
('EMP008', 'Sneha Reddy',    'sneha.reddy@company.com',    '9810000008', 3, 55000.00, '2023-06-01'),
('EMP009', 'Meera Joshi',    'meera.joshi@company.com',    '9810000009', 4, 72000.00, '2020-02-10'),
('EMP010', 'Vikram Nair',    'vikram.nair@company.com',    '9810000010', 4, 50000.00, '2024-05-15'),
('EMP011', 'Arjun Mehta',    'arjun.mehta@company.com',    '9810000011', 4, 48000.00, '2025-01-06'),
('EMP012', 'Kiran Rao',      'kiran.rao@company.com',      '9810000012', 5, 67000.00, '2021-09-27'),
('EMP013', 'Pooja Menon',    'pooja.menon@company.com',    '9810000013', 5, 53000.00, '2022-12-01'),
('EMP014', 'Rohan Das',      'rohan.das@company.com',      NULL,         5, 39000.00, '2025-07-14'),
('EMP015', 'Tanya Bose',     'tanya.bose@company.com',     '9810000015', 1, 76000.00, '2022-03-21');
