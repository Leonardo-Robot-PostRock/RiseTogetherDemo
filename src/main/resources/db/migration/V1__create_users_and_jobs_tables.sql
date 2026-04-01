CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_username ON users(username);

CREATE TABLE user_roles (
    user_id CHAR(36) NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE jobs (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    company VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    salary_min DOUBLE,
    salary_max DOUBLE,
    currency VARCHAR(10),
    employment_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_job_title ON jobs(title);
CREATE INDEX idx_job_company ON jobs(company);

CREATE TABLE job_skills (
    job_id CHAR(36) NOT NULL,
    skill VARCHAR(100) NOT NULL,
    PRIMARY KEY (job_id, skill),
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);
