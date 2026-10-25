CREATE TABLE roles (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(30)  NOT NULL,
    description VARCHAR(200) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name),
    CONSTRAINT ck_roles_name_no_prefix CHECK (name NOT LIKE 'ROLE\_%')
);

CREATE TABLE permissions (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(200) NOT NULL,
    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uq_permissions_name UNIQUE (name)
);

CREATE TABLE role_permissions (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

CREATE TABLE users (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    username              VARCHAR(30)  NOT NULL,
    email                 VARCHAR(120) NOT NULL,
    full_name             VARCHAR(80)  NOT NULL,
    password_hash         CHAR(60)     NOT NULL,
    enabled               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at            DATETIME     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),

    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email),

    CONSTRAINT ck_users_password_is_bcrypt CHECK (password_hash REGEXP '^\\$2[aby]\\$[0-9]{2}\\$')
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

INSERT INTO permissions (name, description) VALUES
    ('EMPLOYEE_READ',   'Read the staff directory'),
    ('EMPLOYEE_WRITE',  'Create and update employee records'),
    ('EMPLOYEE_DELETE', 'Delete employee records'),
    ('USER_MANAGE',     'Enable and disable users, revoke their tokens');

INSERT INTO roles (name, description) VALUES
    ('ADMIN',   'System administrator'),
    ('HR',      'Human resources staff'),
    ('MANAGER', 'Line manager'),
    ('USER',    'Every employee (given to new registrations)');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p
WHERE (r.name = 'ADMIN')
   OR (r.name = 'HR'      AND p.name IN ('EMPLOYEE_READ', 'EMPLOYEE_WRITE'))
   OR (r.name = 'MANAGER' AND p.name IN ('EMPLOYEE_READ'))
   OR (r.name = 'USER'    AND p.name IN ('EMPLOYEE_READ'));
