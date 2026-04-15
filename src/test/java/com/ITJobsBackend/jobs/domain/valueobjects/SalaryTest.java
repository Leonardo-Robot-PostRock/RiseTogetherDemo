package com.ITJobsBackend.jobs.domain.valueobjects;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ITJobsBackend.shared.domain.exceptions.ValidationException;

class SalaryTest {

  @Test
  void shouldCreateSalaryWithValidValues() {
    // When
    Salary salary = Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD");

    // Then
    assertEquals(new BigDecimal("80000"), salary.min());
    assertEquals(new BigDecimal("120000"), salary.max());
    assertEquals("USD", salary.currency());
  }

  @Test
  void shouldNormalizeCurrencyToUpperCase() {
    // When
    Salary salary = Salary.of(new BigDecimal("50000"), new BigDecimal("70000"), "usd");

    // Then
    assertEquals("USD", salary.currency());
  }

  @Test
  void shouldThrowWhenMinIsNull() {
    // When & Then
    assertThrows(ValidationException.class, () -> Salary.of(null, new BigDecimal("100000"), "USD"));
  }

  @Test
  void shouldThrowWhenMinIsNegative() {
    // When & Then
    assertThrows(
        ValidationException.class,
        () -> Salary.of(new BigDecimal("-1000"), new BigDecimal("100000"), "USD"));
  }

  @Test
  void shouldThrowWhenMaxIsNull() {
    // When & Then
    assertThrows(ValidationException.class, () -> Salary.of(new BigDecimal("50000"), null, "USD"));
  }

  @Test
  void shouldThrowWhenMaxIsLessThanMin() {
    // When & Then
    assertThrows(
        ValidationException.class,
        () -> Salary.of(new BigDecimal("100000"), new BigDecimal("50000"), "USD"));
  }

  @Test
  void shouldThrowWhenMaxEqualsMin() {
    // When & Then
    assertThrows(
        ValidationException.class,
        () -> Salary.of(new BigDecimal("50000"), new BigDecimal("50000"), "USD"));
  }

  @Test
  void shouldThrowWhenCurrencyIsNull() {
    // When & Then
    assertThrows(
        ValidationException.class,
        () -> Salary.of(new BigDecimal("50000"), new BigDecimal("100000"), null));
  }

  @Test
  void shouldThrowWhenCurrencyIsBlank() {
    // When & Then
    assertThrows(
        ValidationException.class,
        () -> Salary.of(new BigDecimal("50000"), new BigDecimal("100000"), "   "));
  }

  @Test
  void shouldBeEqualWhenValuesMatch() {
    // Given
    Salary salary1 = Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD");
    Salary salary2 = Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD");

    // Then
    assertEquals(salary1, salary2);
    assertEquals(salary1.hashCode(), salary2.hashCode());
  }

  @Test
  void shouldNotBeEqualWhenValuesDiffer() {
    // Given
    Salary salary1 = Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD");
    Salary salary2 = Salary.of(new BigDecimal("90000"), new BigDecimal("120000"), "USD");

    // Then
    assertNotEquals(salary1, salary2);
  }

  @Test
  void shouldReturnFormattedToString() {
    // Given
    Salary salary = Salary.of(new BigDecimal("80000"), new BigDecimal("120000"), "USD");

    // When & Then
    assertEquals("USD 80000 - 120000", salary.toString());
  }
}
