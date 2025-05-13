CREATE SCHEMA IF NOT EXISTS user_schema;
SET
search_path TO user_schema;

CREATE TABLE jwt_sessions
(
    jti             UUID                     NOT NULL PRIMARY KEY,
    employee_number VARCHAR(200)             NOT NULL,
    app_type        VARCHAR(50)              NOT NULL,
    issued_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at      TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Index pour accélérer les recherches par employeeNumber et appType
CREATE INDEX idx_jwt_sessions_employee_app
    ON jwt_sessions (employee_number, app_type);

CREATE TABLE session_limits
(
    id           BIGSERIAL PRIMARY KEY,
    app_type     VARCHAR(16) NOT NULL UNIQUE,
    max_sessions SMALLINT    NOT NULL CHECK (max_sessions > 0),
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at   TIMESTAMP WITH TIME ZONE
);

