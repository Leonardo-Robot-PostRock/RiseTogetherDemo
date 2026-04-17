package com.ITJobsBackend.profiles.infrastructure.adapters.out.persistence.jpa.mappers;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.profiles.domain.aggregate.EmployerAggregate;
import com.ITJobsBackend.profiles.infrastructure.adapters.out.persistence.jpa.entities.EmployerEntity;
import com.ITJobsBackend.shared.domain.valueobjects.EmployerId;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@Component
public class EmployerMapper {

  public EmployerAggregate toDomain(EmployerEntity entity) {
    if (entity == null) {
      return null;
    }

    return EmployerAggregate.reconstitute(
        EmployerId.of(entity.getId()),
        UserId.of(entity.getId()),
        entity.getCompanyName(),
        entity.getIndustry(),
        entity.getWebsite(),
        entity.getLocation(),
        entity.getContactPerson(),
        entity.getContactEmail(),
        entity.getLogoUrl(),
        entity.getDescription(),
        entity.getCompanySize(),
        Timestamp.of(entity.getCreatedAt()),
        Timestamp.of(entity.getUpdatedAt()));
  }

  public EmployerEntity toEntity(EmployerAggregate employer) {
    if (employer == null) {
      return null;
    }

    EmployerEntity entity = new EmployerEntity();
    entity.setId(employer.getId().value());
    entity.setCompanyName(employer.getCompanyName());
    entity.setIndustry(employer.getIndustry());
    entity.setWebsite(employer.getWebsite());
    entity.setLocation(employer.getLocation());
    entity.setContactPerson(employer.getContactPerson());
    entity.setContactEmail(employer.getContactEmail());
    entity.setLogoUrl(employer.getLogoUrl());
    entity.setDescription(employer.getDescription());
    entity.setCompanySize(employer.getCompanySize());
    entity.setCreatedAt(employer.getCreatedAt().value());
    entity.setUpdatedAt(employer.getUpdatedAt().value());
    return entity;
  }
}
