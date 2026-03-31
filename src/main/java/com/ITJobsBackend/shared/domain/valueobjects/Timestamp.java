package com.ITJobsBackend.shared.domain.valueobjects;

import java.time.Instant;
import java.util.Objects;

public final class Timestamp {
  private final Instant value;

  private Timestamp(Instant value) {
    this.value = value;
  }

  public static Timestamp now() {
    return new Timestamp(Instant.now());
  }

  public static Timestamp of(Instant instant) {
    if (instant == null) {
      throw new IllegalArgumentException("Timestamp cannot be null");
    }
    return new Timestamp(instant);
  }

  public Instant value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Timestamp)) return false;
    Timestamp other = (Timestamp) o;
    return Objects.equals(value, other.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
