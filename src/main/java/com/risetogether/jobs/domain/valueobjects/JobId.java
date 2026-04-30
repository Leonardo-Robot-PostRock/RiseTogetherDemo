package com.risetogether.jobs.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

import com.risetogether.shared.domain.valueobjects.Identifier;

/**
 * Value object representing the unique identity of a
 * {@link com.risetogether.jobs.domain.aggregate.JobAggregate}.
 *
 * <p>Backed by a {@link UUID} and stored as {@code CHAR(36)} in the database.
 * Use the factory methods:
 * <ul>
 *   <li>{@link #generate()} — new random identifier</li>
 *   <li>{@link #of(String)} — parse from UUID string</li>
 *   <li>{@link #of(UUID)} — wrap an existing UUID</li>
 * </ul>
 */
public record JobId(UUID value) implements Identifier {

  public JobId {
    Objects.requireNonNull(value, "JobId cannot be null");
  }

  /** @return a new randomly-generated {@code JobId} */
  public static JobId generate() {
    return new JobId(UUID.randomUUID());
  }

  /**
   * @param id UUID string representation
   * @return parsed {@code JobId}
   * @throws IllegalArgumentException if the string is not a valid UUID
   */
  public static JobId of(String id) {
    try {
      return new JobId(UUID.fromString(id));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid JobId format: " + id);
    }
  }

  /**
   * @param uuid existing UUID
   * @return wrapped {@code JobId}
   */
  public static JobId of(UUID uuid) {
    return new JobId(uuid);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
