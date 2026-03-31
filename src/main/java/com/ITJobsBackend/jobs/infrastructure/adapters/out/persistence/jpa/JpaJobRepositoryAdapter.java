package com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa;

import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;
import com.ITJobsBackend.jobs.domain.repository.JobRepository;
import com.ITJobsBackend.jobs.domain.specification.JobSpecification;
import com.ITJobsBackend.jobs.domain.valueobjects.JobId;
import com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa.mappers.JobMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

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
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<JobAggregate> findAll(JobSpecification spec) {
    return findAll().stream().filter(spec::isSatisfiedBy).collect(Collectors.toList());
  }

  @Override
  public List<JobAggregate> searchByTitle(String title) {
    return jpaRepository.searchByTitle(title).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }
}
