ALTER TABLE candidates
    ADD COLUMN years_of_experience      SMALLINT NULL,
    ADD COLUMN desired_salary_min       DECIMAL(15,2) NULL,
    ADD COLUMN desired_salary_max       DECIMAL(15,2) NULL,
    ADD COLUMN desired_employment_type  VARCHAR(20) NULL,
    ADD COLUMN open_to_remote           BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN available_for_freelance  BOOLEAN NOT NULL DEFAULT FALSE;