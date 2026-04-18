package com.ITJobsBackend.jobs.infrastructure.adapters.in.rest;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ITJobsBackend.jobs.application.ports.in.CreateJobPort;
import com.ITJobsBackend.jobs.application.ports.in.SearchJobsPort;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.JobResponse;
import com.ITJobsBackend.jobs.infrastructure.adapters.in.rest.dto.CreateJobRequest;
import com.ITJobsBackend.jobs.infrastructure.adapters.in.rest.mappers.JobRestMapper;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final CreateJobPort createJobPort;
    private final SearchJobsPort searchJobsPort;
    private final JobRestMapper jobRestMapper;

    public JobController(
            CreateJobPort createJobPort,
            SearchJobsPort searchJobsPort,
            JobRestMapper jobRestMapper) {
        this.createJobPort = createJobPort;
        this.searchJobsPort = searchJobsPort;
        this.jobRestMapper = jobRestMapper;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest request) {
        JobResponse response = createJobPort.execute(jobRestMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> searchJobs(
            @RequestParam(required = false) String title) {
        List<JobResponse> response = searchJobsPort.execute(jobRestMapper.toQuery(title));
        return ResponseEntity.ok(response);
    }
}
