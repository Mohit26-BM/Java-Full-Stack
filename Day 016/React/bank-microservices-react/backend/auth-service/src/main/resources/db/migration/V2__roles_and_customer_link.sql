-- CUSTOMER (default), TELLER or ADMIN. Every existing user becomes a CUSTOMER until an admin changes it.
-- customer_id links a login to a customer record; a CUSTOMER can only see and use the accounts of that customer.
ALTER TABLE user_credentials
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    ADD COLUMN customer_id BIGINT NULL;
