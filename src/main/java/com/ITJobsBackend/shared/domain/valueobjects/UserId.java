package com.ITJobsBackend.shared.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing the unique identity of a {@code UserAggregate}.
 *
 * <p>Backed by a {@link UUID} and stored as {@code CHAR(36)} in the database.
 * Use the factory methods:
 * <ul>
 *   <li>{@link #generate()} — new random identifier</li>
 *   <li>{@link #of(String)} — parse from UUID string</li>
 *   <li>{@link #of(UUID)} — wrap an existing UUID</li>
 * </ul>
 */
public record UserId(UUID value) implements Identifier {

  public UserId {
    Objects.requireNonNull(value, "UserId cannot be null");
  }

  /** @return a new randomly-generated {@code UserId} */
  public static UserId generate() {
    return new UserId(UUID.randomUUID());
  }

  /**
   * @param id UUID string representation
   * @return parsed {@code UserId}
   * @throws IllegalArgumentException if the string is not a valid UUID
   */
  public static UserId of(String id) {
    try {
      return new UserId(UUID.fromString(id));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UserId format: " + id);
    }
  }

  /**
   * @param uuid existing UUID
   * @return wrapped {@code UserId}
   */
  public static UserId of(UUID uuid) {
    return new UserId(uuid);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
