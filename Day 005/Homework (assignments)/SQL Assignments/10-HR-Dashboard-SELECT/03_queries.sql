-- HR Dashboard - employee counts, department summaries and salary distribution

USE hr_dashboard_db;

-- employee counts

-- q1: total employees on record
SELECT COUNT(*) AS total_employees FROM employees;

-- q2: employees by status
SELECT status, COUNT(*) AS employee_count
FROM employees
GROUP BY status
ORDER BY employee_count DESC;

-- q3: current headcount by department, including departments with no staff
SELECT d.dept_name, COUNT(e.emp_id) AS headcount
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id AND e.status <> 'Resigned'
GROUP BY d.dept_id, d.dept_name
ORDER BY headcount DESC, d.dept_name;

-- q4: current headcount by employment type
SELECT employment_type, COUNT(*) AS headcount
FROM employees
WHERE status <> 'Resigned'
GROUP BY employment_type
ORDER BY headcount DESC;

-- q5: current headcount by job level
SELECT job_level, COUNT(*) AS headcount
FROM employees
WHERE status <> 'Resigned'
GROUP BY job_level
ORDER BY job_level;

-- q6: number of hires per year
SELECT YEAR(hire_date) AS hire_year, COUNT(*) AS hires
FROM employees
GROUP BY YEAR(hire_date)
ORDER BY hire_year;

-- department summaries

-- q7: salary summary per department for current employees
SELECT d.dept_name,
       COUNT(e.emp_id)         AS headcount,
       SUM(e.salary)           AS total_salary,
       ROUND(AVG(e.salary), 2) AS average_salary,
       MIN(e.salary)           AS lowest_salary,
       MAX(e.salary)           AS highest_salary
FROM departments d
JOIN employees e ON d.dept_id = e.dept_id
WHERE e.status <> 'Resigned'
GROUP BY d.dept_id, d.dept_name
ORDER BY total_salary DESC;

-- q8: salary cost compared with department budget
SELECT d.dept_name,
       d.annual_budget,
       SUM(e.salary)                                AS salary_cost,
       d.annual_budget - SUM(e.salary)              AS remaining_budget,
       ROUND(SUM(e.salary) / d.annual_budget * 100, 1) AS percent_used
FROM departments d
JOIN employees e ON d.dept_id = e.dept_id
WHERE e.status <> 'Resigned'
GROUP BY d.dept_id, d.dept_name, d.annual_budget
ORDER BY percent_used DESC;

-- q9: departments with 5 or more current employees
SELECT d.dept_name, COUNT(e.emp_id) AS headcount
FROM departments d
JOIN employees e ON d.dept_id = e.dept_id
WHERE e.status <> 'Resigned'
GROUP BY d.dept_id, d.dept_name
HAVING COUNT(e.emp_id) >= 5
ORDER BY headcount DESC;

-- q10: departments with no employees
SELECT d.dept_name, d.location
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
WHERE e.emp_id IS NULL;

-- salary distribution

-- q11: number of current employees in each salary band
SELECT CASE
           WHEN salary < 50000  THEN '1. Under 50,000'
           WHEN salary < 75000  THEN '2. 50,000 - 74,999'
           WHEN salary < 100000 THEN '3. 75,000 - 99,999'
           ELSE '4. 100,000 and above'
       END AS salary_band,
       COUNT(*) AS employee_count
FROM employees
WHERE status <> 'Resigned'
GROUP BY salary_band
ORDER BY salary_band;

-- q12: salary range and average by job level
SELECT job_level,
       COUNT(*)              AS headcount,
       MIN(salary)           AS lowest_salary,
       ROUND(AVG(salary), 2) AS average_salary,
       MAX(salary)           AS highest_salary
FROM employees
WHERE status <> 'Resigned'
GROUP BY job_level
ORDER BY job_level;

-- q13: top 5 earners
SELECT e.first_name, e.last_name, d.dept_name, e.job_title, e.salary
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
WHERE e.status <> 'Resigned'
ORDER BY e.salary DESC
LIMIT 5;

-- q14: current employees earning under 50000
SELECT e.first_name, e.last_name, d.dept_name, e.employment_type, e.salary
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
WHERE e.status <> 'Resigned'
  AND e.salary < 50000
ORDER BY e.salary;
