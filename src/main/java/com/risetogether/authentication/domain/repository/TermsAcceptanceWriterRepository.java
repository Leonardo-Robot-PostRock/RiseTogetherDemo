package com.risetogether.authentication.domain.repository;

import com.risetogether.authentication.domain.entity.TermsAcceptance;

/**
 * Write-side repository port for {@link TermsAcceptance} audit records.
 *
 * <p>Used by {@code RegisterUserUseCase} and {@code GoogleAuthUseCase} after a user accepts
 * the terms of service during sign-up.
 */
public interface TermsAcceptanceWriterRepository {

  /**
   * Persists a new {@link TermsAcceptance} audit record.
   *
   * @param acceptance the acceptance record to save
   */
  void save(TermsAcceptance acceptance);
}
