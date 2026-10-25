-- =====================================================================
-- Sample data from the assignment (section 22).
-- Lives in db/sample, which application.properties adds to Flyway's
-- locations, so the demo database gets it but the test database does not.
-- Version 1.1 runs right after V1 (the tables) and before V2.
-- =====================================================================

INSERT INTO departments (department_id, name, location, active) VALUES
    (1, 'Cardiology',       'Block A, Floor 2', TRUE),
    (2, 'Orthopedics',      'Block B, Floor 1', TRUE),
    (3, 'General Medicine', 'Block A, Floor 1', TRUE),
    (4, 'Pediatrics',       'Block C, Floor 3', TRUE);

INSERT INTO doctors (doctor_id, doctor_code, name, specialization, email, phone, consultation_fee, active, department_id) VALUES
    (1, 'D001', 'Dr. Anil Kumar',  'Cardiology',       'anil.kumar@hospital.com',  '9876500001', 800.00, TRUE, 1),
    (2, 'D002', 'Dr. Meera Rao',   'Orthopedics',      'meera.rao@hospital.com',   '9876500002', 700.00, TRUE, 2),
    (3, 'D003', 'Dr. Ravi Sharma', 'General Medicine', 'ravi.sharma@hospital.com', '9876500003', 500.00, TRUE, 3);

INSERT INTO patients (patient_id, patient_number, name, date_of_birth, gender, email, phone, address, active) VALUES
    (1, 'P1001', 'Rajesh Kumar',  '1985-04-12', 'MALE',   'rajesh.kumar@example.com',  '9000000001', 'Bengaluru', TRUE),
    (2, 'P1002', 'Suresh Reddy',  '1978-11-30', 'MALE',   'suresh.reddy@example.com',  '9000000002', 'Hyderabad', TRUE),
    (3, 'P1003', 'Anitha Sharma', '1992-07-08', 'FEMALE', 'anitha.sharma@example.com', '9000000003', 'Chennai',   TRUE);

-- The fee is copied from the doctor (D001 = 800.00) as the booking service would do.
INSERT INTO appointments (appointment_id, appointment_number, patient_id, doctor_id, appointment_date,
                          start_time, end_time, status, reason, fee, created_at, updated_at, version) VALUES
    (1, 'A10001', 1, 1, '2026-10-05', '10:00:00', '10:30:00', 'BOOKED',    'Chest pain',      800.00, NOW(), NOW(), 0),
    (2, 'A10002', 2, 1, '2026-10-05', '10:30:00', '11:00:00', 'CONFIRMED', 'Routine checkup', 800.00, NOW(), NOW(), 0);
