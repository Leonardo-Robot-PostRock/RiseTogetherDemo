package com.ITJobsBackend.jobs.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public final class JobId {
  private final UUID value;

  private JobId(UUID value) {
    this.value = value;
  }

  public static JobId generate() {
    return new JobId(UUID.randomUUID());
  }

  public static JobId of(String id) {
    try {
      return new JobId(UUID.fromString(id));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid JobId format: " + id);
    }
  }

  public static JobId of(UUID uuid) {
    return new JobId(uuid);
  }

  public UUID value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof JobId)) return false;
    JobId jobId = (JobId) o;
    return Objects.equals(value, jobId.value);
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
