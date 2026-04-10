package com.ITJobsBackend.jobs.application.ports.in;

import java.util.List;

import com.ITJobsBackend.jobs.application.usecases.searchjobs.JobResponse;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.SearchJobsQuery;

public interface SearchJobsPort {
  List<JobResponse> execute(SearchJobsQuery query);
}
