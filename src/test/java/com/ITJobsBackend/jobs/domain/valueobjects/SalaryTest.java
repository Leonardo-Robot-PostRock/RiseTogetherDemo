package com.ITJobsBackend.jobs.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SalaryTest {

  @Test
  void shouldCreateSalaryWithValidValues() {
    Salary salary = Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD");

    assertEquals(new BigDecimal("80000"), salary.min());
    assertEquals(new BigDecimal("120000"), salary.max());
    assertEquals("USD", salary.currency());
  }

  @Test
  void shouldNormalizeCurrencyToUpperCase() {
    Salary salary = Salary.of(new BigDecimal("50000"), new BigDecimal("70000"), "usd");

    assertEquals("USD", salary.currency());
  }

  @Test
  void shouldThrowWhenMinIsNull() {
    assertThrows(ValidationException.class, () -> Salary.of(null, new BigDecimal("100000"), "USD"));
  }

  @Test
  void shouldThrowWhenMinIsNegative() {
    assertThrows(
        ValidationException.class,
        () -> Salary.of(new BigDecimal("-1000"), new BigDecimal("100000"), "USD"));
  }

  @Test
  void shouldThrowWhenMaxIsNull() {
    assertThrows(ValidationException.class, () -> Salary.of(new BigDecimal("50000"), null, "USD"));
  }

  @Test
  void shouldThrowWhenMaxIsLessThanMin() {
    assertThrows(
        ValidationException.class,
        () -> Salary.of(new BigDecimal("100000"), new BigDecimal("50000"), "USD"));
  }

  @Test
  void shouldThrowWhenMaxEqualsMin() {
    assertThrows(
        ValidationException.class,
        () -> Salary.of(new BigDecimal("50000"), new BigDecimal("50000"), "USD"));
  }

  @Test
  void shouldThrowWhenCurrencyIsNull() {
    assertThrows(
        ValidationException.class,
        () -> Salary.of(new BigDecimal("50000"), new BigDecimal("100000"), null));
  }

  @Test
  void shouldThrowWhenCurrencyIsBlank() {
    assertThrows(
        ValidationException.class,
        () -> Salary.of(new BigDecimal("50000"), new BigDecimal("100000"), "   "));
  }

  @Test
  void shouldBeEqualWhenValuesMatch() {
    Salary salary1 = Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD");
    Salary salary2 = Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD");

    assertEquals(salary1, salary2);
    assertEquals(salary1.hashCode(), salary2.hashCode());
  }

  @Test
  void shouldNotBeEqualWhenValuesDiffer() {
    Salary salary1 = Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD");
    Salary salary2 = Salary.of(new BigDecimal("90000"), new BigDecimal("120000"), "USD");

    assertNotEquals(salary1, salary2);
  }

  @Test
  void shouldReturnFormattedToString() {
    Salary salary = Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD");

    assertEquals("USD 80000 - 120000", salary.toString());
  }
}
