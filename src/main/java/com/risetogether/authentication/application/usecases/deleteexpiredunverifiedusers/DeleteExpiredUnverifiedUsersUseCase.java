package com.risetogether.authentication.application.usecases.deleteexpiredunverifiedusers;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.risetogether.authentication.application.ports.in.DeleteExpiredUnverifiedUsersPort;
import com.risetogether.authentication.application.ports.out.DeleteUserPort;
import com.risetogether.authentication.application.ports.out.QueryUserPort;

@Service
@Transactional
public class DeleteExpiredUnverifiedUsersUseCase implements DeleteExpiredUnverifiedUsersPort {
  private static final Logger log =
      LoggerFactory.getLogger(DeleteExpiredUnverifiedUsersUseCase.class);

  private final QueryUserPort queryUserPort;
  private final DeleteUserPort deleteUserPort;

  public DeleteExpiredUnverifiedUsersUseCase(
      QueryUserPort queryUserPort, DeleteUserPort deleteUserPort) {
    this.queryUserPort = queryUserPort;
    this.deleteUserPort = deleteUserPort;
  }

  @Override
  public void execute() {
    Instant cutoff = Instant.now().minusSeconds(86400 * 2);
    log.debug("Deleting unverified users created before: {}", cutoff);

    var idsToDelete = queryUserPort.findUnverifiedUserIdsBefore(cutoff);
    log.info("Found {} unverified users to delete", idsToDelete.size());

    for (var userId : idsToDelete) {
      deleteUserPort.delete(userId);
      log.debug("Deleted unverified user: {}", userId);
    }
  }
}
