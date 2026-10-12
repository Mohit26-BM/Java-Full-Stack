-- Employee Data Management - delete inactive employees safely

USE employee_dml_db;

-- turn off safe update mode for this script
SET SQL_SAFE_UPDATES = 0;

-- inactive employees and how many assets they still hold
SELECT e.emp_id, e.first_name, e.last_name, COUNT(a.asset_id) AS assets_held
FROM employees e
LEFT JOIN assets a ON e.emp_id = a.emp_id
WHERE e.status = 'Inactive'
GROUP BY e.emp_id, e.first_name, e.last_name
ORDER BY e.emp_id;

-- delete inactive employees who hold no assets
DELETE FROM employees
WHERE status = 'Inactive'
  AND emp_id NOT IN (SELECT emp_id FROM assets WHERE emp_id IS NOT NULL);

-- inactive employees kept because they still hold assets
SELECT e.emp_id, e.first_name, e.last_name, a.asset_tag, a.asset_type
FROM employees e
JOIN assets a ON e.emp_id = a.emp_id
WHERE e.status = 'Inactive'
ORDER BY e.emp_id;

-- row counts after delete
SELECT COUNT(*) AS employee_rows FROM employees;

SELECT status, COUNT(*) AS employee_count
FROM employees
GROUP BY status;

-- turn safe update mode back on
SET SQL_SAFE_UPDATES = 1;
