-- ─────────────────────────────────────────────────────────────────────────────
-- Employee Service – DDL
-- Database : H2 (in-memory)
-- ─────────────────────────────────────────────────────────────────────────────

DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS auth_users;

CREATE TABLE employees
(
    id                       BIGINT          NOT NULL AUTO_INCREMENT,
    first_name               VARCHAR(50)     NOT NULL,
    middle_name              VARCHAR(50),
    last_name                VARCHAR(50)     NOT NULL,
    last_mother_name         VARCHAR(50)     NOT NULL,
    age                      VARCHAR(3)      NOT NULL,
    sex                      VARCHAR(1)      NOT NULL CHECK (sex IN ('M', 'F', 'O')),
    birth_date               DATE            NOT NULL,
    position                 VARCHAR(100)    NOT NULL,
    system_registration_date TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                   BOOLEAN         NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_employees PRIMARY KEY (id)
);

CREATE TABLE auth_users
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,          -- BCrypt hashed
    role     VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER',

    CONSTRAINT pk_auth_users PRIMARY KEY (id)
);
