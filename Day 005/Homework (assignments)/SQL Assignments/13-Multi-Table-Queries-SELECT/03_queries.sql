-- Multi-Table Queries - joins across employees, departments and projects

USE multi_table_db;

-- join types

-- q1: inner join - only employees who have a department
SELECT e.first_name, e.last_name, e.job_title, d.dept_name
FROM employees e
INNER JOIN departments d ON e.dept_id = d.dept_id
ORDER BY d.dept_name, e.last_name;

-- q2: left join - all employees, department shown if they have one
SELECT e.first_name, e.last_name, e.job_title, d.dept_name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id
ORDER BY d.dept_name, e.last_name;

-- q3: right join - all departments, employees shown if there are any
SELECT d.dept_name, e.first_name, e.last_name
FROM employees e
RIGHT JOIN departments d ON e.dept_id = d.dept_id
ORDER BY d.dept_name, e.last_name;

-- q4: self join - each employee with their manager's name
SELECT e.first_name, e.last_name, e.job_title,
       m.first_name AS manager_first_name,
       m.last_name  AS manager_last_name
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.emp_id
ORDER BY e.emp_id;

-- joins across three and four tables

-- q5: projects with their owning department
SELECT p.project_name, d.dept_name AS owning_department, p.start_date, p.end_date, p.budget
FROM projects p
JOIN departments d ON p.dept_id = d.dept_id
ORDER BY p.start_date;

-- q6: who works on which project, with both the employee's and the project's department
SELECT e.first_name, e.last_name,
       ed.dept_name AS employee_department,
       p.project_name,
       pd.dept_name AS project_department,
       ep.role, ep.hours_per_week
FROM employee_projects ep
JOIN employees e    ON ep.emp_id = e.emp_id
JOIN departments ed ON e.dept_id = ed.dept_id
JOIN projects p     ON ep.project_id = p.project_id
JOIN departments pd ON p.dept_id = pd.dept_id
ORDER BY p.project_name, ep.role, e.last_name;

-- q7: employees working on another department's project
SELECT e.first_name, e.last_name,
       ed.dept_name AS employee_department,
       p.project_name,
       pd.dept_name AS project_department
FROM employee_projects ep
JOIN employees e    ON ep.emp_id = e.emp_id
JOIN departments ed ON e.dept_id = ed.dept_id
JOIN projects p     ON ep.project_id = p.project_id
JOIN departments pd ON p.dept_id = pd.dept_id
WHERE ed.dept_id <> pd.dept_id
ORDER BY e.last_name;

-- q8: ongoing projects with their lead
SELECT p.project_name, e.first_name AS lead_first_name, e.last_name AS lead_last_name, p.start_date
FROM projects p
JOIN employee_projects ep ON p.project_id = ep.project_id
JOIN employees e          ON ep.emp_id = e.emp_id
WHERE ep.role = 'Lead'
  AND p.end_date IS NULL
ORDER BY p.start_date;

-- joins with grouping

-- q9: team size and weekly hours per project, including projects with no team
SELECT p.project_name,
       COUNT(ep.emp_id)       AS team_size,
       SUM(ep.hours_per_week) AS weekly_hours
FROM projects p
LEFT JOIN employee_projects ep ON p.project_id = ep.project_id
GROUP BY p.project_id, p.project_name
ORDER BY team_size DESC, p.project_name;

-- q10: number of projects and weekly hours per employee, including employees with none
SELECT e.first_name, e.last_name,
       COUNT(ep.project_id)   AS projects,
       SUM(ep.hours_per_week) AS weekly_hours
FROM employees e
LEFT JOIN employee_projects ep ON e.emp_id = ep.emp_id
GROUP BY e.emp_id, e.first_name, e.last_name
ORDER BY projects DESC, e.last_name;

-- q11: employees and projects per department
SELECT d.dept_name,
       COUNT(DISTINCT e.emp_id)     AS employees,
       COUNT(DISTINCT p.project_id) AS projects
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
LEFT JOIN projects p  ON d.dept_id = p.dept_id
GROUP BY d.dept_id, d.dept_name
ORDER BY d.dept_name;

-- finding missing matches

-- q12: employees not assigned to any project
SELECT e.first_name, e.last_name, e.job_title
FROM employees e
LEFT JOIN employee_projects ep ON e.emp_id = ep.emp_id
WHERE ep.project_id IS NULL;

-- q13: projects with no employees assigned
SELECT p.project_name, d.dept_name
FROM projects p
JOIN departments d ON p.dept_id = d.dept_id
LEFT JOIN employee_projects ep ON p.project_id = ep.project_id
WHERE ep.emp_id IS NULL;

-- q14: departments with no employees
SELECT d.dept_name, d.location
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
WHERE e.emp_id IS NULL;
