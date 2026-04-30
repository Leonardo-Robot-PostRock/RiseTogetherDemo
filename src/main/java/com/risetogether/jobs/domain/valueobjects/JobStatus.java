package com.risetogether.jobs.domain.valueobjects;

/**
 * Represents the current lifecycle state of a {@link com.risetogether.jobs.domain.aggregate.JobAggregate}.
 *
 * <ul>
 *   <li>{@code OPEN}     — actively accepting applications</li>
 *   <li>{@code CLOSED}   — no longer accepting applications (terminal state)</li>
 *   <li>{@code DRAFT}    — created but not yet published</li>
 *   <li>{@code EXPIRED}  — past the posting expiry date</li>
 *   <li>{@code INACTIVE} — temporarily hidden by the owner or an admin</li>
 * </ul>
 */
public enum JobStatus {
  OPEN,
  CLOSED,
  DRAFT,
  EXPIRED,
  INACTIVE
}
