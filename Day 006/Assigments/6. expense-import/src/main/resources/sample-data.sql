-- =========================================================
-- Expense Import and Monthly Reporting - Sample Data
-- Run after schema.sql
--
-- Only employees are inserted here. Expenses are loaded by
-- the application from expenses.csv (menu option 2).
-- =========================================================

USE expense_reporting;

INSERT INTO employees (employee_code, employee_name) VALUES
('EMP001', 'Ravi Kumar'),
('EMP002', 'Priya Sharma'),
('EMP003', 'Amit Verma'),
('EMP004', 'Neha Iyer'),
('EMP005', 'Karan Patel'),
('EMP006', 'Sneha Reddy');
