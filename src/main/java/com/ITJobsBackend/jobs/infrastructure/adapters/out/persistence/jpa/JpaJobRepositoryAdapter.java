package com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa;

import com.ITJobsBackend.jobs.domain.model.Job;
import com.ITJobsBackend.jobs.domain.repository.JobRepository;
import com.ITJobsBackend.jobs.domain.valueobjects.JobId;
import com.ITJobsBackend.jobs.infrastructure.adapters.out.persistence.jpa.mappers.JobMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaJobRepositoryAdapter implements JobRepository {
    private final SpringDataJpaJobRepository jpaRepository;
    private final JobMapper mapper;

    public JpaJobRepositoryAdapter(
            SpringDataJpaJobRepository jpaRepository,
            JobMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Job save(Job job) {
        var entity = mapper.toEntity(job);

        Optional.ofNullable(entity.getId())
                .ifPresent(id -> {
                    if (id == null) {
                        throw new IllegalArgumentException("Job ID cannot be null");
                    }

                    if (!jpaRepository.existsById(id)) {
                        throw new IllegalArgumentException("Job with ID " + id + " does not exist");
                    }
                });

        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Job> findById(JobId id) {
        return Optional.ofNullable(id.value())
                .flatMap(jpaRepository::findById)
                .map(mapper::toDomain)
                .or(Optional::empty);
    }

    @Override
    public List<Job> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Job> searchByTitle(String title) {
        return jpaRepository.searchByTitle(title).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
