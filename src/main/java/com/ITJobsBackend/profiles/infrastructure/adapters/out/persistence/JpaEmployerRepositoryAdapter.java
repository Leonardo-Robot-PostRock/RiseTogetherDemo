package com.ITJobsBackend.profiles.infrastructure.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.ITJobsBackend.profiles.domain.aggregate.EmployerAggregate;
import com.ITJobsBackend.profiles.domain.repository.EmployerReaderRepository;
import com.ITJobsBackend.profiles.domain.repository.EmployerWriterRepository;
import com.ITJobsBackend.profiles.infrastructure.adapters.out.persistence.jpa.entities.EmployerEntity;
import com.ITJobsBackend.profiles.infrastructure.adapters.out.persistence.jpa.mappers.EmployerMapper;
import com.ITJobsBackend.shared.domain.valueobjects.EmployerId;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@Repository
public class JpaEmployerRepositoryAdapter implements EmployerReaderRepository, EmployerWriterRepository {

  private final SpringDataJpaEmployerRepository jpaRepository;
  private final EmployerMapper mapper;

  public JpaEmployerRepositoryAdapter(
      SpringDataJpaEmployerRepository jpaRepository, EmployerMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public EmployerAggregate save(EmployerAggregate employer) {
    EmployerEntity entity = mapper.toEntity(employer);
    EmployerEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public void delete(EmployerAggregate employer) {
    EmployerEntity entity = mapper.toEntity(employer);
    jpaRepository.delete(entity);
  }

  @Override
  public Optional<EmployerAggregate> findById(EmployerId id) {
    return Optional.ofNullable(id.value()).flatMap(jpaRepository::findById).map(mapper::toDomain);
  }

  @Override
  public Optional<EmployerAggregate> findByUserId(UserId userId) {
    return jpaRepository.findByUserId(userId.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<EmployerAggregate> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public boolean existsByUserId(UserId userId) {
    return jpaRepository.existsById(userId.value());
  }

  @Override
  public boolean existsByCompanyName(String companyName) {
    return jpaRepository.existsByCompanyName(companyName);
  }
}