package com.risetogether.jobs.infrastructure.adapters.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.risetogether.jobs.domain.aggregate.JobAggregate;
import com.risetogether.jobs.domain.repository.JobRepository;
import com.risetogether.jobs.domain.specification.JobSpecification;
import com.risetogether.jobs.domain.valueobjects.JobId;
import com.risetogether.jobs.infrastructure.adapters.out.persistence.jpa.mappers.JobMapper;

@Repository
public class JpaJobRepositoryAdapter implements JobRepository {
  private final SpringDataJpaJobRepository jpaRepository;
  private final JobMapper mapper;

  public JpaJobRepositoryAdapter(SpringDataJpaJobRepository jpaRepository, JobMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public JobAggregate save(JobAggregate job) {
    var entity = mapper.toEntity(job);

    @SuppressWarnings("null")
    var savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<JobAggregate> findById(JobId id) {
    return Optional.ofNullable(id.value()).flatMap(jpaRepository::findById).map(mapper::toDomain);
  }

  @Override
  public List<JobAggregate> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<JobAggregate> findAll(JobSpecification spec) {
    return findAll().stream().filter(spec::isSatisfiedBy).toList();
  }

  @Override
  public List<JobAggregate> searchByTitle(String title) {
    return jpaRepository.searchByTitle(title).stream().map(mapper::toDomain).toList();
  }
}
