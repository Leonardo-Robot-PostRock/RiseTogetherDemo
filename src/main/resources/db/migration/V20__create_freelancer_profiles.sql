CREATE TABLE freelancer_profiles (
    id               CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci PRIMARY KEY,
    headline         VARCHAR(150) NOT NULL,
    hourly_rate_min  DECIMAL(10,2) NULL,
    hourly_rate_max  DECIMAL(10,2) NULL,
    currency         VARCHAR(10) NULL,
    availability     VARCHAR(20) NOT NULL DEFAULT 'ON_DEMAND',
    portfolio_url    VARCHAR(500) NULL,
    bio              TEXT NULL,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);