-- Add employer_id FK to jobs (RF-12)
ALTER TABLE jobs ADD COLUMN employer_id CHAR(36);
ALTER TABLE jobs ADD CONSTRAINT fk_jobs_employer FOREIGN KEY (employer_id) REFERENCES employers(id) ON DELETE SET NULL;
CREATE INDEX idx_job_employer ON jobs(employer_id);