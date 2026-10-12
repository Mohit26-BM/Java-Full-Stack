-- Employee Data Management - select queries

USE employee_dml_db;

-- q1: all employees with their department
SELECT e.emp_id, e.first_name, e.last_name, d.dept_name, e.job_title, e.salary, e.status
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
ORDER BY d.dept_name, e.last_name;

-- q2: number of employees in each department
SELECT d.dept_name, COUNT(e.emp_id) AS employee_count
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
GROUP BY d.dept_id, d.dept_name
ORDER BY d.dept_id;

-- q3: salary summary per department for active employees
SELECT d.dept_name,
       SUM(e.salary)           AS total_salary,
       ROUND(AVG(e.salary), 2) AS average_salary,
       MIN(e.salary)           AS lowest_salary,
       MAX(e.salary)           AS highest_salary
FROM departments d
JOIN employees e ON d.dept_id = e.dept_id
WHERE e.status = 'Active'
GROUP BY d.dept_id, d.dept_name
ORDER BY average_salary DESC;

-- q4: active employees earning over 90000
SELECT first_name, last_name, job_title, salary
FROM employees
WHERE status = 'Active'
  AND salary > 90000
ORDER BY salary DESC;

-- q5: number of active and inactive employees
SELECT status, COUNT(*) AS employee_count
FROM employees
GROUP BY status;

-- q6: all assets and who holds them
SELECT a.asset_tag, a.asset_type, e.first_name, e.last_name, e.status
FROM assets a
LEFT JOIN employees e ON a.emp_id = e.emp_id
ORDER BY a.asset_tag;

-- q7: inactive employees who still need to return assets
SELECT e.first_name, e.last_name, d.dept_name, a.asset_tag, a.asset_type
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
JOIN assets a      ON e.emp_id = a.emp_id
WHERE e.status = 'Inactive'
ORDER BY e.last_name;

-- q8: departments with no employees
SELECT d.dept_id, d.dept_name
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
WHERE e.emp_id IS NULL;
