package com.ITJobsBackend.jobs.infrastructure.adapters.in.rest;

import com.ITJobsBackend.jobs.application.usecases.createjob.CreateJobCommand;
import com.ITJobsBackend.jobs.application.usecases.createjob.CreateJobUseCase;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.SearchJobsQuery;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.SearchJobsUseCase;
import com.ITJobsBackend.jobs.domain.aggregate.JobAggregate;
import com.ITJobsBackend.jobs.infrastructure.adapters.in.rest.dto.CreateJobRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<Map<String, Object>> createJob(
      @Valid @RequestBody CreateJobRequest request) {
    CreateJobCommand command = new CreateJobCommand(
        request.title(),
        request.description(),
        request.company(),
        request.location(),
        request.salaryMin(),
        request.salaryMax(),
        request.currency(),
        request.employmentType()
    );
    JobAggregate job = createJobUseCase.execute(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(job));
  }

  @GetMapping
  public ResponseEntity<List<Map<String, Object>>> searchJobs(
      @RequestParam(required = false) String title) {
    SearchJobsQuery query = new SearchJobsQuery(title);
    List<JobAggregate> jobs = searchJobsUseCase.execute(query);
    List<Map<String, Object>> response =
        jobs.stream().map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  private Map<String, Object> toResponse(JobAggregate job) {
    return Map.of(
        "id", job.getId().value().toString(),
        "title", job.getTitle(),
        "description", job.getDescription() != null ? job.getDescription() : "",
        "company", job.getCompany(),
        "location", job.getLocation() != null ? job.getLocation() : "",
        "salary",
            Map.of(
                "min", job.getSalary().min(),
                "max", job.getSalary().max(),
                "currency", job.getSalary().currency()),
        "employmentType", job.getEmploymentType().name(),
        "status", job.getStatus().name(),
        "skills", job.getSkills(),
        "createdAt", job.getCreatedAt().value().toString());
  }
}
