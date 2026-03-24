package com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa.mappers;

import com.ITJobsBackend.jobs.domain.model.Job;
import com.ITJobsBackend.jobs.domain.valueobjects.JobId;
import com.ITJobsBackend.jobs.domain.valueobjects.Salary;
import com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa.entities.JobEntity;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public JobEntity toEntity(Job domain) {
        JobEntity entity = new JobEntity();
        entity.setId(domain.getId().value());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setCompany(domain.getCompany());
        entity.setLocation(domain.getLocation());
        entity.setSalaryMin(domain.getSalary().min());
        entity.setSalaryMax(domain.getSalary().max());
        entity.setCurrency(domain.getSalary().currency());
        entity.setEmploymentType(domain.getEmploymentType());
        entity.setStatus(domain.getStatus());
        entity.setSkills(domain.getSkills());
        entity.setCreatedAt(domain.getCreatedAt().value());
        entity.setUpdatedAt(domain.getUpdatedAt().value());
        return entity;
    }

    public Job toDomain(JobEntity entity) {
        return Job.reconstitute(
            JobId.of(entity.getId()),
            entity.getTitle(),
            entity.getDescription(),
            entity.getCompany(),
            entity.getLocation(),
            Salary.of(entity.getSalaryMin(), entity.getSalaryMax(), entity.getCurrency()),
            entity.getEmploymentType(),
            entity.getStatus(),
            entity.getSkills(),
            Timestamp.of(entity.getCreatedAt()),
            Timestamp.of(entity.getUpdatedAt())
        );
    }
}
