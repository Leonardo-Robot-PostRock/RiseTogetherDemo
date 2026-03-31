package com.ITJobsBackend.shared.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

public final class UserId {

  private final UUID value;

  private UserId(UUID value) {
    this.value = Objects.requireNonNull(value, "UserId cannot be null");
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

  public UUID value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserId userId)) return false;
    return value.equals(userId.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
