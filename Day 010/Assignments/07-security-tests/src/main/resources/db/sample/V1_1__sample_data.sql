INSERT INTO employees (id, name, email, department, designation, salary, manager_id) VALUES
    (1, 'Aditi Rao',      'aditi.rao@company.com',      'Management',      'Director',            250000, NULL),
    (2, 'Hana Kapoor',    'hana.kapoor@company.com',    'Human Resources', 'HR Manager',          140000, 1),
    (3, 'Manoj Pillai',   'manoj.pillai@company.com',   'Engineering',     'Engineering Manager', 160000, 1),
    (4, 'Asha Verma',     'asha.verma@company.com',     'Engineering',     'Software Engineer',    95000, 3),
    (5, 'Ravi Iyer',      'ravi.iyer@company.com',      'Engineering',     'Software Engineer',    92000, 3),
    (6, 'Meena Das',      'meena.das@company.com',      'Human Resources', 'HR Executive',         70000, 2),
    (7, 'Karan Shah',     'karan.shah@company.com',     'Finance',         'Accountant',           80000, 1);

INSERT INTO users (username, password_hash, employee_id) VALUES
    ('admin', '$2a$10$kOgeb4kHaday1Qka07YUTOX7g41dLUctpUZvxlQEo5mN/fXnpz.E.', 1),
    ('hana',  '$2a$10$FJP1bozMttTspZngBiTZGOYMYnzxhFK256qNz7GSYqF71ibKjwNhC', 2),
    ('manoj', '$2a$10$/F6ELGmwVSbnnFET6cDLoudnW5EyqGs/xGH7S21r1rw7sAE1CWNAm', 3),
    ('asha',  '$2a$10$clL4DrDv/4T6xux8kdP1AOYJzIoz.puLOYTx9ATyati.njDo/.Juu', 4),
    ('ravi',  '$2a$10$6xI2fqkXQMACWmErc2lCVe80S9xZn2AbDQoS.CycCdJGxE1UCpWhe', 5);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r
WHERE (u.username = 'admin' AND r.name = 'ADMIN')
   OR (u.username = 'hana'  AND r.name = 'HR')
   OR (u.username = 'manoj' AND r.name = 'MANAGER')
   OR (u.username IN ('asha', 'ravi') AND r.name = 'EMPLOYEE');
