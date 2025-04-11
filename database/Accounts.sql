USE accounts;

DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(50) NOT NULL
) COMMENT 'Users';

INSERT INTO users (username, password, enabled, role) VALUES
('admin', 'admin', TRUE, 'ADMIN'),
('user', 'user', TRUE, 'USER'),
('1', '1', TRUE, 'USER');

CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50) NOT NULL UNIQUE,
    permission VARCHAR(100) NOT NULL,
    CHECK (role IN ('ADMIN', 'USER'))
) COMMENT 'Roles';

INSERT INTO roles (role, permission) VALUES
('ADMIN', 'ALL'),
('USER', 'WRITE');