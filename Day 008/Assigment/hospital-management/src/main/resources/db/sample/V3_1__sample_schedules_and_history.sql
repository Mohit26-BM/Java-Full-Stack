-- =====================================================================
-- Sample data for the extension tables (runs after V3 on the demo database only).
-- =====================================================================

-- Working hours. D001 and D002: Monday to Saturday 09:00-17:00 in 30-minute slots.
-- D003 (General Medicine): Monday to Friday 09:00-13:00 in 15-minute slots.
INSERT INTO doctor_working_hours (doctor_id, day_of_week, start_time, end_time, slot_minutes) VALUES
    (1, 'MONDAY',    '09:00', '17:00', 30), (1, 'TUESDAY',  '09:00', '17:00', 30), (1, 'WEDNESDAY', '09:00', '17:00', 30),
    (1, 'THURSDAY',  '09:00', '17:00', 30), (1, 'FRIDAY',   '09:00', '17:00', 30), (1, 'SATURDAY',  '09:00', '17:00', 30),
    (2, 'MONDAY',    '09:00', '17:00', 30), (2, 'TUESDAY',  '09:00', '17:00', 30), (2, 'WEDNESDAY', '09:00', '17:00', 30),
    (2, 'THURSDAY',  '09:00', '17:00', 30), (2, 'FRIDAY',   '09:00', '17:00', 30), (2, 'SATURDAY',  '09:00', '17:00', 30),
    (3, 'MONDAY',    '09:00', '13:00', 15), (3, 'TUESDAY',  '09:00', '13:00', 15), (3, 'WEDNESDAY', '09:00', '13:00', 15),
    (3, 'THURSDAY',  '09:00', '13:00', 15), (3, 'FRIDAY',   '09:00', '13:00', 15);

-- The audit trail of the two sample appointments
INSERT INTO appointment_status_changes (appointment_id, from_status, to_status, changed_at, changed_by, note) VALUES
    (1, NULL,     'BOOKED',    NOW(), 'SAMPLE_DATA', 'Booked'),
    (2, NULL,     'BOOKED',    NOW(), 'SAMPLE_DATA', 'Booked'),
    (2, 'BOOKED', 'CONFIRMED', NOW(), 'SAMPLE_DATA', NULL);
