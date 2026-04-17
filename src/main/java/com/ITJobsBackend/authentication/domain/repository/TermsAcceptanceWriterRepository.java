package com.ITJobsBackend.authentication.domain.repository;

import com.ITJobsBackend.authentication.domain.entity.TermsAcceptance;

public interface TermsAcceptanceWriterRepository {

  void save(TermsAcceptance acceptance);
}
