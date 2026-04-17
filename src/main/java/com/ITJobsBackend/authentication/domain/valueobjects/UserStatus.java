package com.ITJobsBackend.authentication.domain.valueobjects;

/**
 * Encapsulates the lifecycle state of a user, derived from the combination of
 * {@code active} and {@code emailVerified} flags.
 *
 * <ul>
 *   <li>{@code PENDING_VERIFICATION} — newly registered, email not yet verified, account inactive
 *   <li>{@code ACTIVE} — email verified and account active
 *   <li>{@code SUSPENDED} — account deactivated (was previously active)
 * </ul>
 */
public enum UserStatus {
  PENDING_VERIFICATION,
  ACTIVE,
  SUSPENDED;

  /**
   * Derives the status from the two legacy boolean columns stored in the database.
   * Used exclusively by the persistence mapper when reconstituting the aggregate.
   */
  public static UserStatus from(boolean active, boolean emailVerified) {
    if (active) return ACTIVE;
    if (emailVerified) return SUSPENDED;
    return PENDING_VERIFICATION;
  }

  public boolean isActive() {
    return this == ACTIVE;
  }

  public boolean isEmailVerified() {
    return this == ACTIVE || this == SUSPENDED;
  }
}

