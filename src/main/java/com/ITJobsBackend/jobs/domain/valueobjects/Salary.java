package com.ITJobsBackend.jobs.domain.valueobjects;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;
import java.util.Objects;

public final class Salary {
    private final double min;
    private final double max;
    private final String currency;

    private Salary(double min, double max, String currency) {
        this.min = min;
        this.max = max;
        this.currency = currency;
    }

    public static Salary of(double min, double max, String currency) {
        if (min < 0) {
            throw new ValidationException("Minimum salary cannot be negative");
        }
        if (max < min) {
            throw new ValidationException("Maximum salary cannot be less than minimum");
        }
        if (currency == null || currency.isBlank()) {
            throw new ValidationException("Currency is required");
        }
        return new Salary(min, max, currency.toUpperCase());
    }

    public double min() { return min; }
    public double max() { return max; }
    public String currency() { return currency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Salary)) return false;
        Salary salary = (Salary) o;
        return Double.compare(salary.min, min) == 0
            && Double.compare(salary.max, max) == 0
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
