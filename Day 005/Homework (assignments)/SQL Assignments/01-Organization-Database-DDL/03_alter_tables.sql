-- Organization Database - add salary and joining_date to employees

USE org_db;

-- structure before alter
DESCRIBE employees;

-- add new columns
ALTER TABLE employees
    ADD COLUMN salary       DECIMAL(10,2) AFTER dept_id,
    ADD COLUMN joining_date DATE AFTER salary;

-- fill values for existing employees
UPDATE employees SET salary =  75000.00, joining_date = '2018-03-12' WHERE emp_id = 1;
UPDATE employees SET salary =  52000.00, joining_date = '2023-08-01' WHERE emp_id = 2;
UPDATE employees SET salary =  90000.00, joining_date = '2016-11-20' WHERE emp_id = 3;
UPDATE employees SET salary =  61000.00, joining_date = '2021-05-17' WHERE emp_id = 4;
UPDATE employees SET salary = 110000.00, joining_date = '2015-01-05' WHERE emp_id = 5;
UPDATE employees SET salary =  98000.00, joining_date = '2019-09-23' WHERE emp_id = 6;
UPDATE employees SET salary =  67000.00, joining_date = '2024-02-12' WHERE emp_id = 7;
UPDATE employees SET salary =  72000.00, joining_date = '2020-06-08' WHERE emp_id = 8;
UPDATE employees SET salary =  55000.00, joining_date = '2024-10-01' WHERE emp_id = 9;
UPDATE employees SET salary =  64000.00, joining_date = '2022-04-18' WHERE emp_id = 10;

-- check for missing values
SELECT COUNT(*) AS missing_values
FROM employees
WHERE salary IS NULL OR joining_date IS NULL;

-- make new columns not null
ALTER TABLE employees
    MODIFY COLUMN salary       DECIMAL(10,2) NOT NULL,
    MODIFY COLUMN joining_date DATE NOT NULL;

-- structure after alter
DESCRIBE employees;
SELECT emp_id, first_name, last_name, salary, joining_date FROM employees;
