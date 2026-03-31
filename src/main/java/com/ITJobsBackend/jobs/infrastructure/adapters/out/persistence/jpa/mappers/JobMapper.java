package com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa.mappers;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;
import com.ITJobsBackend.jobs.domain.valueobjects.JobId;
import com.ITJobsBackend.jobs.domain.valueobjects.Salary;
import com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa.entities.JobEntity;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

  public JobEntity toEntity(JobAggregate domain) {
    return JobEntity.fromDomain(domain);
  }

  public JobAggregate toDomain(JobEntity entity) {
    return JobAggregate.reconstitute(
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
        Timestamp.of(entity.getUpdatedAt()));
  }
}
