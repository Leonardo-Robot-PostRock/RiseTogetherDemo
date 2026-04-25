package com.ITJobsBackend.authentication.application.ports.out;

import com.ITJobsBackend.shared.domain.valueobjects.UserId;

/**
 * Write-side port for deleting a {@code UserAggregate}.
 *
 * <p>Separated from {@link SaveUserPort} by SRP: creation/update and deletion are
 * distinct concerns that evolve independently.
 */
public interface DeleteUserPort {

  /**
   * Permanently removes the user identified by the given ID.
   *
   * @param id the unique identifier of the user to delete
   */
  void delete(UserId id);
}

