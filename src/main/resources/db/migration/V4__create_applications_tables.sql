-- Applications and Saved Jobs (candidate actions on jobs)

CREATE TABLE applications (
    id CHAR(36) PRIMARY KEY,
    job_id CHAR(36) NOT NULL,
    candidate_id CHAR(36) NOT NULL,
    application_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    resume VARCHAR(255),
    cover_letter TEXT,
    FOREIGN KEY (job_id) REFERENCES jobs(id),
    FOREIGN KEY (candidate_id) REFERENCES candidates(id)
);

CREATE INDEX idx_application_candidate ON applications(candidate_id);
CREATE INDEX idx_application_job ON applications(job_id);
CREATE INDEX idx_application_status ON applications(status);

CREATE TABLE saved_jobs (
    id CHAR(36) PRIMARY KEY,
    candidate_id CHAR(36) NOT NULL,
    job_id CHAR(36) NOT NULL,
    saved_at TIMESTAMP NOT NULL,
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    UNIQUE (candidate_id, job_id)
);

CREATE INDEX idx_saved_jobs_candidate ON saved_jobs(candidate_id);
