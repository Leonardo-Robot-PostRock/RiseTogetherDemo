package com.risetogether.authentication.application.ports.out;

import com.risetogether.authentication.domain.entity.TermsAcceptance;

public interface SaveTermsAcceptancePort {

  void save(TermsAcceptance acceptance);
}
