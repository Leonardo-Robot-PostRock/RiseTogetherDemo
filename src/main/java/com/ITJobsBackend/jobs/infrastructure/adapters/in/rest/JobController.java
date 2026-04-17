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

import com.ITJobsBackend.jobs.application.usecases.createjob.CreateJobCommand;
import com.ITJobsBackend.jobs.application.usecases.createjob.CreateJobUseCase;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.JobResponse;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.SearchJobsQuery;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.SearchJobsUseCase;
import com.ITJobsBackend.jobs.infrastructure.adapters.in.rest.dto.CreateJobRequest;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {
  private final CreateJobUseCase createJobUseCase;
  private final SearchJobsUseCase searchJobsUseCase;

  public JobController(CreateJobUseCase createJobUseCase, SearchJobsUseCase searchJobsUseCase) {
    this.createJobUseCase = createJobUseCase;
    this.searchJobsUseCase = searchJobsUseCase;
  }

  @PostMapping
  public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest request) {
    CreateJobCommand command =
        new CreateJobCommand(
            request.title(),
            request.description(),
            request.company(),
            request.location(),
            request.salaryMin(),
            request.salaryMax(),
            request.currency(),
            request.employmentType(),
            request.employerId());

    JobResponse response = createJobUseCase.execute(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<JobResponse>> searchJobs(
      @RequestParam(required = false) String title) {
    SearchJobsQuery query = new SearchJobsQuery(title);
    List<JobResponse> response = searchJobsUseCase.execute(query);
    return ResponseEntity.ok(response);
  }
}
