-- Sample employees (run after schema.sql)
USE employee_portal;

INSERT INTO employees (name, email, department, designation, salary, status) VALUES
    ('Asha Rao',   'asha.rao@company.com',   'Engineering', 'Senior Engineer',   1440000.00, 'ACTIVE'),
    ('Ravi Kumar', 'ravi.kumar@company.com', 'Finance',     'Accountant',         840000.00, 'ACTIVE'),
    ('Meera Iyer', 'meera.iyer@company.com', 'HR',          'HR Business Partner', 960000.00, 'ON_LEAVE');
