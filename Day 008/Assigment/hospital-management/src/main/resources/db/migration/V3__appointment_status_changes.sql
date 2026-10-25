-- =====================================================================
-- V3 - extension: audit table for appointment status changes.
-- One row per change, written in the SAME transaction as the change itself,
-- so a rolled-back change leaves no audit row either.
-- =====================================================================

-- Appointment 1 ---- * AppointmentStatusChange
CREATE TABLE appointment_status_changes (
    status_change_id BIGINT       AUTO_INCREMENT PRIMARY KEY,
    appointment_id   BIGINT       NOT NULL,
    from_status      VARCHAR(20),                  -- NULL for the first row (the booking)
    to_status        VARCHAR(20)  NOT NULL,
    changed_at       DATETIME     NOT NULL,
    changed_by       VARCHAR(50)  NOT NULL,        -- API, NO_SHOW_JOB, SAMPLE_DATA
    note             VARCHAR(255),
    CONSTRAINT fk_status_changes_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (appointment_id)
);

CREATE INDEX idx_status_changes_appointment ON appointment_status_changes (appointment_id, changed_at);
