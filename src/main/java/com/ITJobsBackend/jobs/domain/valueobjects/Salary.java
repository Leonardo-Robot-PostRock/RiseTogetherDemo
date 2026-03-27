package com.ITJobsBackend.jobs.domain.valueobjects;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

import java.math.BigDecimal;
import java.util.Objects;

public final class Salary {
    private final BigDecimal min;
    private final BigDecimal max;
    private final String currency;

    private Salary(BigDecimal min, BigDecimal max, String currency) {
        this.min = min;
        this.max = max;
        this.currency = currency;
    }

    public static Salary of(BigDecimal min, BigDecimal max, String currency) {
        if (min == null || min.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Minimum salary cannot be negative");
        }
        
        if (max == null || max.compareTo(min) < 0) {
            throw new ValidationException("Maximum salary cannot be less than minimum");
        }

        if (currency == null || currency.isBlank()) {
            throw new ValidationException("Currency is required");
        }
        return new Salary(min, max, currency.toUpperCase());
    }

    public BigDecimal min() { return min; }
    public BigDecimal max() { return max; }
    public String currency() { return currency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Salary)) return false;

        Salary salary = (Salary) o;
        
        return Objects.equals(min, salary.min)
            && Objects.equals(max, salary.max)
            && Objects.equals(currency, salary.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, currency);
    }

    @Override
    public String toString() {
        return currency + " " + min + " - " + max;
    }
}
