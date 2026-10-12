-- Capstone HR Management - inserts, updates and deletes for HR operations (today is 2025-10-06)

USE hr_management_db;

-- turn off safe update mode for this script
SET SQL_SAFE_UPDATES = 0;

-- operation 1: new hire joins Human Resources
INSERT INTO employees (emp_id, first_name, last_name, email, phone, hire_date, job_id, dept_id, manager_id, salary)
VALUES (26, 'Nadia', 'Rahman', 'nadia.rahman@example.com', '555-7026', '2025-10-06', 2, 1, 1, 50000.00);

-- operation 2: 5% raise for active Engineering staff, recorded in salary history
START TRANSACTION;
INSERT INTO salary_history (emp_id, old_salary, new_salary, change_date, change_reason)
SELECT emp_id, salary, ROUND(salary * 1.05, 2), '2025-10-06', 'Annual Raise'
FROM employees
WHERE dept_id = 3 AND status = 'Active';

UPDATE employees
SET salary = ROUND(salary * 1.05, 2)
WHERE dept_id = 3 AND status = 'Active';
COMMIT;

-- operation 3: Kevin Wright promoted from QA Engineer to Software Engineer
START TRANSACTION;
INSERT INTO salary_history (emp_id, old_salary, new_salary, change_date, change_reason)
SELECT emp_id, salary, 88000.00, '2025-10-06', 'Promotion'
FROM employees
WHERE emp_id = 14;

UPDATE employees
SET job_id = 6, salary = 88000.00
WHERE emp_id = 14;
COMMIT;

-- operation 4: raise Bilal Ahmed to the Accountant minimum salary
START TRANSACTION;
INSERT INTO salary_history (emp_id, old_salary, new_salary, change_date, change_reason)
SELECT emp_id, salary, 55000.00, '2025-10-06', 'Pay Range Correction'
FROM employees
WHERE emp_id = 7;

UPDATE employees
SET salary = 55000.00
WHERE emp_id = 7;
COMMIT;

-- operation 5: approve and reject pending leave requests
UPDATE leave_requests SET status = 'Approved' WHERE leave_id = 11;
UPDATE leave_requests SET status = 'Rejected' WHERE leave_id = 13;

-- operation 6: Tom Reed leaves the company
UPDATE employees
SET status = 'Terminated', termination_date = '2025-10-06'
WHERE emp_id = 25;

-- operation 7: delete rejected leave requests that ended before 2025-07-01
DELETE FROM leave_requests
WHERE status = 'Rejected'
  AND end_date < '2025-07-01';

-- check results
SELECT emp_id, first_name, last_name, job_id, salary, status, termination_date
FROM employees
WHERE emp_id IN (7, 14, 25, 26) OR dept_id = 3
ORDER BY emp_id;

SELECT * FROM salary_history WHERE change_date = '2025-10-06';
SELECT * FROM leave_requests ORDER BY leave_id;

-- row counts after changes
SELECT COUNT(*) AS employee_rows FROM employees;
SELECT COUNT(*) AS history_rows  FROM salary_history;
SELECT COUNT(*) AS leave_rows    FROM leave_requests;

-- turn safe update mode back on
SET SQL_SAFE_UPDATES = 1;
