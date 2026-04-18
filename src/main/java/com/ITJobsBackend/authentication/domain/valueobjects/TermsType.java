package com.ITJobsBackend.authentication.domain.valueobjects;

/**
 * Classifies the type of a legal document published as a
 * {@link com.ITJobsBackend.authentication.domain.aggregate.TermsDocument}.
 *
 * <ul>
 *   <li>{@code TERMS_OF_SERVICE} — platform usage terms</li>
 *   <li>{@code PRIVACY_POLICY} — data privacy and processing notice</li>
 * </ul>
 */
public enum TermsType {
  TERMS_OF_SERVICE,
  PRIVACY_POLICY
}
