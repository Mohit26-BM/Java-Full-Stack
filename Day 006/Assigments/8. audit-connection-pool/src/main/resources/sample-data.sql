-- =========================================================
-- DataSource and Connection Pooling - Sample Data
-- Run after schema.sql (optional - the app creates its own events)
-- =========================================================

USE audit_pool;

INSERT INTO audit_events (event_type, event_message) VALUES
('USER_LOGIN',      'User ravi.kumar logged in'),
('ACCOUNT_VIEWED',  'User ravi.kumar viewed account ACC1001'),
('FUNDS_TRANSFER',  'Rs. 5000.00 transferred from ACC1001 to ACC1002'),
('USER_LOGOUT',     'User ravi.kumar logged out'),
('PASSWORD_CHANGE', 'User priya.sharma changed password');
