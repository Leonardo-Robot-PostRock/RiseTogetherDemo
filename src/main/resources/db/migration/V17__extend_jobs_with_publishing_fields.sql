ALTER TABLE jobs
    ADD COLUMN created_by_user_id            CHAR(36) NULL,
    ADD COLUMN posted_on_behalf_of_employer_id CHAR(36) NULL,
    ADD COLUMN job_type                      VARCHAR(20) NOT NULL DEFAULT 'TRADITIONAL',
    ADD COLUMN remote_allowed                BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN expires_at                    TIMESTAMP NULL,
    ADD COLUMN featured                      BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN featured_until                TIMESTAMP NULL,
    ADD CONSTRAINT fk_job_created_by   FOREIGN KEY (created_by_user_id)
        REFERENCES users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_job_on_behalf_of FOREIGN KEY (posted_on_behalf_of_employer_id)
        REFERENCES employers(id) ON DELETE SET NULL;

CREATE INDEX idx_job_created_by ON jobs(created_by_user_id);
CREATE INDEX idx_job_type       ON jobs(job_type);
CREATE INDEX idx_job_featured   ON jobs(featured);