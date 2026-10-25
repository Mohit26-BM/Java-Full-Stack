-- =====================================================================
-- V2: one-time password reset tokens
--
-- Only the SHA-256 hash of the token is stored (64 hex characters). The
-- token itself is given to the user once; someone who reads this table
-- cannot use the hashes to reset a password.
-- =====================================================================

CREATE TABLE password_reset_tokens (
    id         BIGINT   NOT NULL AUTO_INCREMENT,
    user_id    BIGINT   NOT NULL,
    token_hash CHAR(64) NOT NULL,
    expires_at DATETIME NOT NULL,
    used_at    DATETIME NULL,                           -- set when used: a token works only once
    created_at DATETIME NOT NULL,
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id),
    CONSTRAINT uq_password_reset_tokens_hash UNIQUE (token_hash),
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
