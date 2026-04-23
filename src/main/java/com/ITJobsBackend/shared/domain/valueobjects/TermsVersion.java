package com.ITJobsBackend.shared.domain.valueobjects;

import java.util.Objects;

public record TermsVersion(String value) {

  public TermsVersion {
    Objects.requireNonNull(value, "TermsVersion cannot be null");

    value = value.trim();

    if (value.isBlank()) {
      throw new IllegalArgumentException("Terms version cannot be empty");
    }
  }

  public static TermsVersion of(String version) {
    return new TermsVersion(version);
  }

  public static TermsVersion current() {
    return new TermsVersion("1");
  }

  @Override
  public String toString() {
    return value;
  }
}
