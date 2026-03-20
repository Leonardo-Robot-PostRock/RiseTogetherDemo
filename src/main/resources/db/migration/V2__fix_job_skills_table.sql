-- Fix job_skills table to match JPA @ElementCollection mapping
-- The old schema had (id, job_id, skill_id) referencing a skills table,
-- but the app stores skills as simple strings via @ElementCollection.

DROP TABLE IF EXISTS job_skills;

CREATE TABLE job_skills (
    job_id CHAR(36) NOT NULL,
    skill VARCHAR(100) NOT NULL,
    PRIMARY KEY (job_id, skill),
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);
