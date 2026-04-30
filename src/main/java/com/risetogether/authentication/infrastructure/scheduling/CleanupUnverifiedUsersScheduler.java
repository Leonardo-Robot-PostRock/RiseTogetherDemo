package com.risetogether.authentication.infrastructure.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.risetogether.authentication.application.ports.in.DeleteExpiredUnverifiedUsersPort;

@Component
public class CleanupUnverifiedUsersScheduler {
  private static final Logger log = LoggerFactory.getLogger(CleanupUnverifiedUsersScheduler.class);

  private final DeleteExpiredUnverifiedUsersPort deleteExpiredUnverifiedUsersPort;

  public CleanupUnverifiedUsersScheduler(
      DeleteExpiredUnverifiedUsersPort deleteExpiredUnverifiedUsersPort) {
    this.deleteExpiredUnverifiedUsersPort = deleteExpiredUnverifiedUsersPort;
  }

  @Scheduled(cron = "${auth.cleanup.cron:0 0 2 * * ?}")
  public void cleanupUnverifiedUsers() {
    log.info("Starting scheduled cleanup of expired unverified users");
    try {
      deleteExpiredUnverifiedUsersPort.execute();
      log.info("Scheduled cleanup completed successfully");
    } catch (Exception e) {
      log.error("Error during scheduled cleanup of unverified users", e);
    }
  }
}
