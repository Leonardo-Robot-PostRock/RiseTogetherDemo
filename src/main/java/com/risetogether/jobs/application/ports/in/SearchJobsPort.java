package com.risetogether.jobs.application.ports.in;

import java.util.List;

import com.risetogether.jobs.application.usecases.searchjobs.JobResponse;
import com.risetogether.jobs.application.usecases.searchjobs.SearchJobsQuery;

public interface SearchJobsPort {
  List<JobResponse> execute(SearchJobsQuery query);
}
