-- Employee Reporting - reports by department, salary range and joining year

USE employee_report_db;

-- by department

-- q1: all employees with their department, sorted by department then name
SELECT d.dept_name, e.first_name, e.last_name, e.job_title, e.salary
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
ORDER BY d.dept_name, e.last_name, e.first_name;

-- q2: employees in the IT department, highest salary first
SELECT e.first_name, e.last_name, e.job_title, e.salary
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
WHERE d.dept_name = 'Information Technology'
ORDER BY e.salary DESC;

-- q3: employees in Finance or Sales, sorted by department then salary
SELECT d.dept_name, e.first_name, e.last_name, e.salary
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
WHERE d.dept_name IN ('Finance', 'Sales')
ORDER BY d.dept_name, e.salary DESC;

-- q4: headcount and salary summary per department, including empty departments
SELECT d.dept_name,
       COUNT(e.emp_id)         AS employee_count,
       ROUND(AVG(e.salary), 2) AS average_salary,
       MIN(e.salary)           AS lowest_salary,
       MAX(e.salary)           AS highest_salary
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
GROUP BY d.dept_id, d.dept_name
ORDER BY employee_count DESC, d.dept_name;

-- by salary range

-- q5: employees earning between 50000 and 80000
SELECT first_name, last_name, job_title, salary
FROM employees
WHERE salary BETWEEN 50000 AND 80000
ORDER BY salary;

-- q6: top 5 highest paid employees
SELECT first_name, last_name, job_title, salary
FROM employees
ORDER BY salary DESC
LIMIT 5;

-- q7: departments with an average salary above 75000
SELECT d.dept_name, ROUND(AVG(e.salary), 2) AS average_salary
FROM departments d
JOIN employees e ON d.dept_id = e.dept_id
GROUP BY d.dept_id, d.dept_name
HAVING AVG(e.salary) > 75000
ORDER BY average_salary DESC;

-- by joining year

-- q8: employees who joined in 2022
SELECT first_name, last_name, job_title, joining_date
FROM employees
WHERE YEAR(joining_date) = 2022
ORDER BY joining_date;

-- q9: number of employees who joined each year
SELECT YEAR(joining_date) AS joining_year, COUNT(*) AS employees_joined
FROM employees
GROUP BY YEAR(joining_date)
ORDER BY joining_year;

-- combined filters

-- q10: joined between 2018 and 2020 and earning over 70000, earliest first
SELECT e.first_name, e.last_name, d.dept_name, e.salary, e.joining_date
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
WHERE e.joining_date BETWEEN '2018-01-01' AND '2020-12-31'
  AND e.salary > 70000
ORDER BY e.joining_date;

-- q11: engineers sorted by salary
SELECT first_name, last_name, job_title, salary
FROM employees
WHERE job_title LIKE '%Engineer%'
ORDER BY salary DESC;

-- q12: employees with no phone number
SELECT first_name, last_name, email
FROM employees
WHERE phone IS NULL
ORDER BY last_name;

-- q13: departments with no employees
SELECT d.dept_id, d.dept_name
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
WHERE e.emp_id IS NULL;
