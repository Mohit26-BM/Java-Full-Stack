-- Capstone HR Management - management reports (SELECT)

USE hr_management_db;

-- workforce

-- r1: current headcount by department, including empty departments
SELECT d.dept_name, COUNT(e.emp_id) AS headcount
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id AND e.status <> 'Terminated'
GROUP BY d.dept_id, d.dept_name
ORDER BY headcount DESC, d.dept_name;

-- r2: organisation chart with job title and manager
SELECT e.emp_id, e.first_name, e.last_name, j.job_title, d.dept_name,
       m.first_name AS manager_first_name, m.last_name AS manager_last_name
FROM employees e
JOIN jobs j        ON e.job_id = j.job_id
JOIN departments d ON e.dept_id = d.dept_id
LEFT JOIN employees m ON e.manager_id = m.emp_id
WHERE e.status <> 'Terminated'
ORDER BY d.dept_name, e.manager_id, e.last_name;

-- r3: hires per year
SELECT YEAR(hire_date) AS hire_year, COUNT(*) AS hires
FROM employees
GROUP BY YEAR(hire_date)
ORDER BY hire_year;

-- r4: employees who have left
SELECT e.first_name, e.last_name, d.dept_name, e.hire_date, e.termination_date
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
WHERE e.status = 'Terminated';

-- r5: departments with no employees
SELECT d.dept_name, d.location
FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id
WHERE e.emp_id IS NULL;

-- pay

-- r6: salary summary per department for current employees
SELECT d.dept_name,
       COUNT(e.emp_id)         AS headcount,
       SUM(e.salary)           AS total_salary,
       ROUND(AVG(e.salary), 2) AS average_salary,
       MIN(e.salary)           AS lowest_salary,
       MAX(e.salary)           AS highest_salary
FROM departments d
JOIN employees e ON d.dept_id = e.dept_id
WHERE e.status <> 'Terminated'
GROUP BY d.dept_id, d.dept_name
ORDER BY total_salary DESC;

-- r7: salary cost compared with department budget
SELECT d.dept_name, d.annual_budget,
       SUM(e.salary)                                   AS salary_cost,
       ROUND(SUM(e.salary) / d.annual_budget * 100, 1) AS percent_used
FROM departments d
JOIN employees e ON d.dept_id = e.dept_id
WHERE e.status <> 'Terminated'
GROUP BY d.dept_id, d.dept_name, d.annual_budget
ORDER BY percent_used DESC;

-- r8: salary bands for current employees
SELECT CASE
           WHEN salary < 50000  THEN '1. Under 50,000'
           WHEN salary < 75000  THEN '2. 50,000 - 74,999'
           WHEN salary < 100000 THEN '3. 75,000 - 99,999'
           ELSE '4. 100,000 and above'
       END AS salary_band,
       COUNT(*) AS employees
FROM employees
WHERE status <> 'Terminated'
GROUP BY salary_band
ORDER BY salary_band;

-- r9: employees paid outside their job's salary range
SELECT e.first_name, e.last_name, j.job_title, e.salary, j.min_salary, j.max_salary
FROM employees e
JOIN jobs j ON e.job_id = j.job_id
WHERE e.status <> 'Terminated'
  AND e.salary NOT BETWEEN j.min_salary AND j.max_salary;

-- r10: employees earning above their department average
SELECT e.first_name, e.last_name, d.dept_name, e.salary, da.average_salary
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
JOIN (SELECT dept_id, ROUND(AVG(salary), 2) AS average_salary
      FROM employees
      WHERE status <> 'Terminated'
      GROUP BY dept_id) AS da ON e.dept_id = da.dept_id
WHERE e.status <> 'Terminated'
  AND e.salary > da.average_salary
ORDER BY d.dept_name, e.salary DESC;

-- r11: salary change history
SELECT e.first_name, e.last_name, sh.change_date, sh.change_reason,
       sh.old_salary, sh.new_salary, sh.new_salary - sh.old_salary AS increase
FROM salary_history sh
JOIN employees e ON sh.emp_id = e.emp_id
ORDER BY sh.change_date, e.last_name, sh.history_id;

-- leave

-- r12: approved leave days per employee
SELECT e.first_name, e.last_name,
       COUNT(l.leave_id)                               AS approved_requests,
       SUM(DATEDIFF(l.end_date, l.start_date) + 1)     AS leave_days
FROM employees e
JOIN leave_requests l ON e.emp_id = l.emp_id
WHERE l.status = 'Approved'
GROUP BY e.emp_id, e.first_name, e.last_name
ORDER BY leave_days DESC, e.last_name;

-- r13: pending leave requests
SELECT e.first_name, e.last_name, l.leave_type, l.start_date, l.end_date
FROM leave_requests l
JOIN employees e ON l.emp_id = e.emp_id
WHERE l.status = 'Pending'
ORDER BY l.start_date;

-- r14: current employees with no approved leave
SELECT e.first_name, e.last_name, d.dept_name
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
LEFT JOIN leave_requests l ON e.emp_id = l.emp_id AND l.status = 'Approved'
WHERE e.status <> 'Terminated'
  AND l.leave_id IS NULL
ORDER BY d.dept_name, e.last_name;
