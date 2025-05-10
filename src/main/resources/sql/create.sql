CREATE SCHEMA IF NOT EXISTS user_schema;
SET search_path TO user_schema;

CREATE TABLE jwt_sessions (
                              jti              UUID             NOT NULL PRIMARY KEY,
                              employee_number  VARCHAR(200)     NOT NULL,
                              app_type         VARCHAR(50)      NOT NULL,
                              issued_at        TIMESTAMP        NOT NULL,
                              expires_at       TIMESTAMP        NOT NULL
);

-- Index pour accélérer les recherches par employeeNumber et appType
CREATE INDEX idx_jwt_sessions_employee_app
    ON jwt_sessions(employee_number, app_type);
