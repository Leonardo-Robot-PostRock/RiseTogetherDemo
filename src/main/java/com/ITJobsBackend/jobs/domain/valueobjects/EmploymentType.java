package com.ITJobsBackend.jobs.domain.valueobjects;

/**
 * Classifies the nature of the employment contract offered by a job listing.
 *
 * <ul>
 *   <li>{@code FULL_TIME}  — permanent, full-time position</li>
 *   <li>{@code PART_TIME}  — permanent, part-time position</li>
 *   <li>{@code CONTRACT}   — fixed-term or project-based contract</li>
 *   <li>{@code FREELANCE}  — independent contractor engagement</li>
 *   <li>{@code INTERNSHIP} — student or graduate internship</li>
 * </ul>
 */
public enum EmploymentType {
  FULL_TIME,
  PART_TIME,
  CONTRACT,
  FREELANCE,
  INTERNSHIP
}
