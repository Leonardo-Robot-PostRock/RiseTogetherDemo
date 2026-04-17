package com.ITJobsBackend.jobs.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

import com.ITJobsBackend.shared.domain.valueobjects.Identifier;

public record JobId(UUID value) implements Identifier {

  public JobId {
    Objects.requireNonNull(value, "JobId cannot be null");
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

    @Override
    public String toString() {
    return value.toString();
  }
}
