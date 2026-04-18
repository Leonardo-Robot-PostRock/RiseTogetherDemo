package com.ITJobsBackend.jobs.domain.valueobjects;

/**
 * Indicates where the work is expected to be performed.
 *
 * <ul>
 *   <li>{@code REMOTE}  — fully remote; no physical office attendance required</li>
 *   <li>{@code HYBRID}  — mix of remote and on-site work</li>
 *   <li>{@code ON_SITE} — must work from the company's premises (default)</li>
 * </ul>
 */
public enum WorkModality {
    REMOTE,
    HYBRID,
    ON_SITE
}
