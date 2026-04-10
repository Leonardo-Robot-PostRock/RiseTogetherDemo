package com.ITJobsBackend.jobs.application.ports.in;

import com.ITJobsBackend.jobs.application.usecases.searchjobs.JobResponse;
import com.ITJobsBackend.jobs.application.usecases.searchjobs.SearchJobsQuery;
import java.util.List;

public interface SearchJobsPort {
  List<JobResponse> execute(SearchJobsQuery query);
}
