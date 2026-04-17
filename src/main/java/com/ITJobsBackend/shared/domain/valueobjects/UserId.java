package com.ITJobsBackend.shared.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) implements Identifier {

  public UserId {
    Objects.requireNonNull(value, "UserId cannot be null");
  }

  public static UserId generate() {
    return new UserId(UUID.randomUUID());
  }

  public static UserId of(String id) {
    try {
      return new UserId(UUID.fromString(id));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UserId format: " + id);
    }
  }

  public static UserId of(UUID uuid) {
    return new UserId(uuid);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
