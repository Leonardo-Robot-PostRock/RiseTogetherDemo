package com.ITJobsBackend.jobs.domain.model;

import com.ITJobsBackend.jobs.domain.valueobjects.EmploymentType;
import com.ITJobsBackend.jobs.domain.valueobjects.JobId;
import com.ITJobsBackend.jobs.domain.valueobjects.JobStatus;
import com.ITJobsBackend.jobs.domain.valueobjects.Salary;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Job {
    private final JobId id;
    private String title;
    private String description;
    private String company;
    private String location;
    private Salary salary;
    private EmploymentType employmentType;
    private JobStatus status;
    private final List<String> skills;
    private final Timestamp createdAt;
    private Timestamp updatedAt;

    private Job(
        JobId id,
        String title,
        String description,
        String company,
        String location,
        Salary salary,
        EmploymentType employmentType,
        Timestamp createdAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.employmentType = employmentType;
        this.status = JobStatus.OPEN;
        this.skills = new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public static Job create(
        String title,
        String description,
        String company,
        String location,
        Salary salary,
        EmploymentType employmentType
    ) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Job title cannot be empty");
        }
        if (company == null || company.isBlank()) {
            throw new ValidationException("Company name cannot be empty");
        }

        return new Job(
            JobId.generate(),
            title,
            description,
            company,
            location,
            salary,
            employmentType,
            Timestamp.now()
        );
    }

    public static Job reconstitute(
        JobId id,
        String title,
        String description,
        String company,
        String location,
        Salary salary,
        EmploymentType employmentType,
        JobStatus status,
        List<String> skills,
        Timestamp createdAt,
        Timestamp updatedAt
    ) {
        Job job = new Job(id, title, description, company, location, salary, employmentType, createdAt);
        job.status = status;
        job.skills.clear();
        job.skills.addAll(skills);
        job.updatedAt = updatedAt;
        return job;
    }

    public void close() {
        this.status = JobStatus.CLOSED;
        this.updatedAt = Timestamp.now();
    }

    public void addSkill(String skill) {
        if (!this.skills.contains(skill)) {
            this.skills.add(skill);
            this.updatedAt = Timestamp.now();
        }
    }

    public JobId getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public Salary getSalary() { return salary; }
    public EmploymentType getEmploymentType() { return employmentType; }
    public JobStatus getStatus() { return status; }
    public List<String> getSkills() { return Collections.unmodifiableList(skills); }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
}
