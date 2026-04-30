package com.risetogether.jobs.domain.valueobjects;

import java.math.BigDecimal;

import com.risetogether.shared.domain.exceptions.ValidationException;

/**
 * Value object representing a salary range for a job listing.
 *
 * <p>Invariants (validated at construction time):
 * <ul>
 *   <li>{@code min} must be non-null and ≥ 0</li>
 *   <li>{@code max} must be non-null and strictly greater than {@code min}</li>
 *   <li>{@code currency} must not be blank; normalised to upper-case (e.g. {@code "USD"})</li>
 * </ul>
 *
 * <p>Use the factory method {@link #of(BigDecimal, BigDecimal, String)} to create instances.
 */
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

  /**
   * @param min      minimum salary (inclusive); must be ≥ 0
   * @param max      maximum salary; must be {@literal >} {@code min}
   * @param currency ISO 4217 currency code (e.g. {@code "USD"})
   * @return a validated {@code Salary} instance
   * @throws ValidationException if any invariant is violated
   */
  public static Salary of(BigDecimal min, BigDecimal max, String currency) {
    return new Salary(min, max, currency);
  }

  @Override
  public String toString() {
    return currency + " " + min + " - " + max;
  }
}
