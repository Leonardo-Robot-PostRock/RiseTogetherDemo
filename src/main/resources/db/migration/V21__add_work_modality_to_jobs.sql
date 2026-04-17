ALTER TABLE jobs
    ADD COLUMN work_modality VARCHAR(20) NOT NULL DEFAULT 'ON_SITE';

CREATE INDEX idx_job_work_modality ON jobs(work_modality);

