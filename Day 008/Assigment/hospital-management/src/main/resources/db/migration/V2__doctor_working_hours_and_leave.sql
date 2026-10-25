-- =====================================================================
-- V2 - extension: doctor working hours and leave.
-- Used to reject bookings outside a doctor's hours or on leave days, and to
-- generate the free appointment slots of a day.
-- =====================================================================

-- Doctor 1 ---- * DoctorWorkingHours   (at most one row per doctor and weekday)
CREATE TABLE doctor_working_hours (
    working_hours_id BIGINT      AUTO_INCREMENT PRIMARY KEY,
    doctor_id        BIGINT      NOT NULL,
    day_of_week      VARCHAR(9)  NOT NULL,              -- MONDAY ... SUNDAY (java.time.DayOfWeek)
    start_time       TIME        NOT NULL,
    end_time         TIME        NOT NULL,
    slot_minutes     INT         NOT NULL DEFAULT 30,   -- length of one generated slot
    CONSTRAINT uq_working_hours_doctor_day UNIQUE (doctor_id, day_of_week),
    CONSTRAINT chk_working_hours_times     CHECK (end_time > start_time),
    CONSTRAINT chk_working_hours_slot      CHECK (slot_minutes BETWEEN 5 AND 240),
    CONSTRAINT fk_working_hours_doctor     FOREIGN KEY (doctor_id) REFERENCES doctors (doctor_id)
);

-- Doctor 1 ---- * DoctorLeave   (inclusive date range: start_date..end_date)
CREATE TABLE doctor_leaves (
    leave_id   BIGINT       AUTO_INCREMENT PRIMARY KEY,
    doctor_id  BIGINT       NOT NULL,
    start_date DATE         NOT NULL,
    end_date   DATE         NOT NULL,
    reason     VARCHAR(255),
    CONSTRAINT chk_doctor_leaves_dates CHECK (end_date >= start_date),
    CONSTRAINT fk_doctor_leaves_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (doctor_id)
);

CREATE INDEX idx_doctor_leaves_doctor_dates ON doctor_leaves (doctor_id, start_date, end_date);
