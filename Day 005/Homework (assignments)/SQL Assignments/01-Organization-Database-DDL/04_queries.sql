-- Organization Database - select queries

USE org_db;

-- q1: employees with their department
SELECT e.emp_id, e.first_name, e.last_name, d.dept_name, d.location
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
ORDER BY d.dept_name, e.last_name;

-- q2: number of employees in each department
SELECT d.dept_name, COUNT(e.emp_id) AS employee_count
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
GROUP BY d.dept_id, d.dept_name
ORDER BY employee_count DESC, d.dept_name;

-- q3: salary totals per department
SELECT d.dept_name,
       COUNT(e.emp_id)         AS employees,
       SUM(e.salary)           AS total_salary,
       ROUND(AVG(e.salary), 2) AS average_salary,
       MAX(e.salary)           AS highest_salary
FROM departments d
JOIN employees e ON d.dept_id = e.dept_id
GROUP BY d.dept_id, d.dept_name
ORDER BY average_salary DESC, d.dept_name;

-- q4: employees joined since 2020 earning over 60000
SELECT first_name, last_name, joining_date, salary
FROM employees
WHERE joining_date >= '2020-01-01'
  AND salary > 60000
ORDER BY joining_date;

-- q5: employees on each project with their role
SELECT p.project_name, d.dept_name AS owning_department,
       e.first_name, e.last_name, ep.role
FROM employee_projects ep
JOIN employees e   ON ep.emp_id = e.emp_id
JOIN projects p    ON ep.project_id = p.project_id
JOIN departments d ON p.dept_id = d.dept_id
ORDER BY p.project_name, ep.role, e.last_name;

-- q6: team size of each project
SELECT p.project_name, COUNT(ep.emp_id) AS team_size
FROM projects p
LEFT JOIN employee_projects ep ON p.project_id = ep.project_id
GROUP BY p.project_id, p.project_name
ORDER BY team_size DESC, p.project_name;

-- q7: employees not assigned to any project
SELECT e.emp_id, e.first_name, e.last_name
FROM employees e
LEFT JOIN employee_projects ep ON e.emp_id = ep.emp_id
WHERE ep.emp_id IS NULL
ORDER BY e.emp_id;

-- q8: ongoing projects
SELECT p.project_name, d.dept_name, p.start_date, p.budget
FROM projects p
JOIN departments d ON p.dept_id = d.dept_id
WHERE p.end_date IS NULL
ORDER BY p.budget DESC;
