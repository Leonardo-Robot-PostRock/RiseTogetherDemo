package com.risetogether.jobs.application.usecases.addskills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.risetogether.jobs.application.ports.in.AddSkillsPort;
import com.risetogether.jobs.application.usecases.searchjobs.JobResponse;
import com.risetogether.jobs.domain.aggregate.JobAggregate;
import com.risetogether.jobs.domain.exceptions.JobNotFoundException;
import com.risetogether.jobs.domain.repository.JobReaderRepository;
import com.risetogether.jobs.domain.repository.JobWriterRepository;
import com.risetogether.jobs.domain.valueobjects.JobId;

@Service
@Transactional
public class AddSkillsUseCase implements AddSkillsPort {

    private static final Logger log = LoggerFactory.getLogger(AddSkillsUseCase.class);

    private final JobReaderRepository jobReaderRepository;
    private final JobWriterRepository jobWriterRepository;

    public AddSkillsUseCase(
            JobReaderRepository jobReaderRepository,
            JobWriterRepository jobWriterRepository) {
        this.jobReaderRepository = jobReaderRepository;
        this.jobWriterRepository = jobWriterRepository;
    }

    @Override
    public JobResponse execute(AddSkillsCommand command) {
        log.info("Adding skills to job: {}", command.jobId());

        JobId jobId = JobId.of(command.jobId());
        JobAggregate job = jobReaderRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId.value().toString()));

        for (String skill : command.skills()) {
            job.addSkill(skill);
        }

        JobAggregate savedJob = jobWriterRepository.save(job);

        log.info("Skills added successfully to job: {}", jobId.value());

        return new JobResponse(
                savedJob.getId().value().toString(),
                savedJob.getTitle(),
                savedJob.getDescription() != null ? savedJob.getDescription() : "",
                savedJob.getCompany(),
                savedJob.getLocation() != null ? savedJob.getLocation() : "",
                savedJob.getSalary().min(),
                savedJob.getSalary().max(),
                savedJob.getSalary().currency(),
                savedJob.getEmploymentType().name(),
                savedJob.getWorkModality().name(),
                savedJob.getStatus().name(),
                savedJob.getCreatedAt().value(),
                savedJob.getSkills());
    }
}