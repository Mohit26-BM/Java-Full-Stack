INSERT INTO users (username, email, full_name, password_hash, enabled, created_at) VALUES
    ('admin',  'aditi.rao@company.com',     'Aditi Rao',     '$2a$10$kOgeb4kHaday1Qka07YUTOX7g41dLUctpUZvxlQEo5mN/fXnpz.E.', TRUE, NOW()),
    ('hana',   'hana.kapoor@company.com',   'Hana Kapoor',   '$2a$10$FJP1bozMttTspZngBiTZGOYMYnzxhFK256qNz7GSYqF71ibKjwNhC', TRUE, NOW()),
    ('manoj',  'manoj.pillai@company.com',  'Manoj Pillai',  '$2a$10$/F6ELGmwVSbnnFET6cDLoudnW5EyqGs/xGH7S21r1rw7sAE1CWNAm', TRUE, NOW()),
    ('asha',   'asha.verma@company.com',    'Asha Verma',    '$2a$10$clL4DrDv/4T6xux8kdP1AOYJzIoz.puLOYTx9ATyati.njDo/.Juu', TRUE, NOW()),
    ('vikram', 'vikram.nair@company.com',   'Vikram Nair',   '$2a$10$cQ6W5RqQp2kvbdEIjWpF8OlHP7htFgjx5NzCvs6GHoykFI6VEj7wa', FALSE, NOW());

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r
WHERE (u.username = 'admin'  AND r.name = 'ADMIN')
   OR (u.username = 'hana'   AND r.name = 'HR')
   OR (u.username = 'manoj'  AND r.name IN ('MANAGER', 'USER'))
   OR (u.username = 'asha'   AND r.name = 'USER')
   OR (u.username = 'vikram' AND r.name = 'USER');
