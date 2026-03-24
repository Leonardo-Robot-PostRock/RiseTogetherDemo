package com.ITJobsBackend.jobs.application.usecases.searchjobs;

import com.ITJobsBackend.jobs.domain.model.Job;
import com.ITJobsBackend.jobs.domain.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchJobsUseCase {
    private final JobRepository jobRepository;

    public SearchJobsUseCase(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> execute(SearchJobsQuery query) {
        if (query.title() != null && !query.title().isBlank()) {
            return jobRepository.searchByTitle(query.title());
        }
        return jobRepository.findAll();
    }
}
