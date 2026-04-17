package com.ITJobsBackend.authentication.application.ports.out;

import com.ITJobsBackend.authentication.domain.entity.TermsAcceptance;

public interface SaveTermsAcceptancePort {

  void save(TermsAcceptance acceptance);
}
