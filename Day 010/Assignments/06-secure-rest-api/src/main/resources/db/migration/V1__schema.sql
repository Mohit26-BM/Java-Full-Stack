CREATE TABLE employees (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    name        VARCHAR(80)   NOT NULL,
    email       VARCHAR(120)  NOT NULL,
    department  VARCHAR(50)   NOT NULL,
    designation VARCHAR(50)   NOT NULL,
    salary      DECIMAL(11,2) NOT NULL,
    manager_id  BIGINT        NULL,
    status      VARCHAR(10)   NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT pk_employees PRIMARY KEY (id),
    CONSTRAINT uq_employees_email UNIQUE (email),
    CONSTRAINT fk_employees_manager FOREIGN KEY (manager_id) REFERENCES employees (id),
    CONSTRAINT ck_employees_salary CHECK (salary > 0),
    CONSTRAINT ck_employees_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE roles (
    id   BIGINT      NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE users (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    username      VARCHAR(30) NOT NULL,
    password_hash CHAR(60)    NOT NULL,
    enabled       BOOLEAN     NOT NULL DEFAULT TRUE,
    employee_id   BIGINT      NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_employee UNIQUE (employee_id),
    CONSTRAINT fk_users_employee FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE SET NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE salary_audit (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    employee_id BIGINT        NOT NULL,
    old_salary  DECIMAL(11,2) NOT NULL,
    new_salary  DECIMAL(11,2) NOT NULL,
    changed_by  VARCHAR(30)   NOT NULL,
    changed_at  DATETIME      NOT NULL,
    CONSTRAINT pk_salary_audit PRIMARY KEY (id)
);

INSERT INTO roles (name) VALUES ('ADMIN'), ('HR'), ('MANAGER'), ('EMPLOYEE');
