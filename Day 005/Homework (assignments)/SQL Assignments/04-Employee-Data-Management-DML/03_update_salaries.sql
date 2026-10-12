-- Employee Data Management - update salaries by department rules

USE employee_dml_db;

-- turn off safe update mode for this script
SET SQL_SAFE_UPDATES = 0;

-- salaries before update
SELECT d.dept_name, COUNT(e.emp_id) AS active_employees,
       SUM(e.salary) AS total_salary, ROUND(AVG(e.salary), 2) AS average_salary
FROM departments d
JOIN employees e ON d.dept_id = e.dept_id
WHERE e.status = 'Active'
GROUP BY d.dept_id, d.dept_name
ORDER BY d.dept_id;

-- rule 1: human resources +5%
UPDATE employees SET salary = salary * 1.05
WHERE dept_id = 1 AND status = 'Active';

-- rule 2: finance +6%
UPDATE employees SET salary = salary * 1.06
WHERE dept_id = 2 AND status = 'Active';

-- rule 3: information technology +10%
UPDATE employees SET salary = salary * 1.10
WHERE dept_id = 3 AND status = 'Active';

-- rule 4: sales +8%
UPDATE employees SET salary = salary * 1.08
WHERE dept_id = 4 AND status = 'Active';

-- rule 5: operations +4%
UPDATE employees SET salary = salary * 1.04
WHERE dept_id = 5 AND status = 'Active';

-- rule 6: minimum salary of 45000 for active employees
UPDATE employees SET salary = 45000.00
WHERE status = 'Active' AND salary < 45000;

-- salaries after update
SELECT d.dept_name, COUNT(e.emp_id) AS active_employees,
       SUM(e.salary) AS total_salary, ROUND(AVG(e.salary), 2) AS average_salary
FROM departments d
JOIN employees e ON d.dept_id = e.dept_id
WHERE e.status = 'Active'
GROUP BY d.dept_id, d.dept_name
ORDER BY d.dept_id;

-- inactive salaries should be unchanged
SELECT emp_id, first_name, last_name, salary
FROM employees
WHERE status = 'Inactive'
ORDER BY emp_id;

-- turn safe update mode back on
SET SQL_SAFE_UPDATES = 1;
