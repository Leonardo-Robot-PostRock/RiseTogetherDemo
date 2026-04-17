package com.ITJobsBackend.shared.domain.valueobjects;

import java.util.Objects;

public final class TermsVersion {
  private final String value;

  private TermsVersion(String value) {
    this.value = Objects.requireNonNull(value, "TermsVersion cannot be null");
  }

  public static TermsVersion of(String version) {
    if (version == null || version.isBlank()) {
      throw new IllegalArgumentException("Terms version cannot be empty");
    }
    return new TermsVersion(version.trim());
  }

  public static TermsVersion current() {
    return new TermsVersion("1");
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TermsVersion that)) return false;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value;
  }
}