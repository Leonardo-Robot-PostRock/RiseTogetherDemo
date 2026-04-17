CREATE TABLE recruiter_employer_associations (
    id               CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci PRIMARY KEY,
    recruiter_user_id CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    employer_id      CHAR(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    role_in_company  VARCHAR(20) NOT NULL DEFAULT 'INTERNAL',
    active           BOOLEAN NOT NULL DEFAULT TRUE,
    started_at       TIMESTAMP NOT NULL,
    ended_at         TIMESTAMP NULL,
    UNIQUE KEY uk_recruiter_employer (recruiter_user_id, employer_id),
    FOREIGN KEY (recruiter_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (employer_id)       REFERENCES employers(id) ON DELETE CASCADE
);

CREATE INDEX idx_rea_recruiter ON recruiter_employer_associations(recruiter_user_id);
CREATE INDEX idx_rea_employer  ON recruiter_employer_associations(employer_id);