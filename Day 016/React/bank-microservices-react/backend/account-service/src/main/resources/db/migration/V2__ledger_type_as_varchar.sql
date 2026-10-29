-- Hibernate created this column as a MySQL ENUM on databases that predate Flyway. A plain VARCHAR is easier to evolve
-- (adding a transaction type no longer needs an ALTER). A no-op on databases created by V1, where it is already VARCHAR.
ALTER TABLE account_transactions MODIFY COLUMN type VARCHAR(20) NOT NULL;
