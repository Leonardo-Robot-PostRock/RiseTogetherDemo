package com.ITJobsBackend.jobs.domain.valueobjects;

import java.math.BigDecimal;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

public record Salary(BigDecimal min, BigDecimal max, String currency) {

  public Salary {
    if (min == null || min.compareTo(BigDecimal.ZERO) < 0) {
      throw new ValidationException("Minimum salary cannot be negative");
    }

    if (max == null || max.compareTo(min) <= 0) {
      throw new ValidationException("Maximum salary must be greater than minimum");
    }

    if (currency == null || currency.isBlank()) {
      throw new ValidationException("Currency is required");
    }
    currency = currency.toUpperCase();
  }

  public static Salary of(BigDecimal min, BigDecimal max, String currency) {
    return new Salary(min, max, currency);
  }

  @Override
  public String toString() {
    return currency + " " + min + " - " + max;
  }
}
