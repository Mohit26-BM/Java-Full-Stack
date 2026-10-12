-- Capstone HR Management - schema changes (DDL)

USE hr_management_db;

-- structure before alter
DESCRIBE employees;

-- add termination date for leavers
ALTER TABLE employees
    ADD COLUMN termination_date DATE AFTER status;

-- make phone column longer
ALTER TABLE employees
    MODIFY COLUMN phone VARCHAR(20);

-- structure after alter
DESCRIBE employees;
SHOW INDEX FROM employees;
