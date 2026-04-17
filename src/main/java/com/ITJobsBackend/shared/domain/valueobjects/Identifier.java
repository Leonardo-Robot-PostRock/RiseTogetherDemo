package com.ITJobsBackend.shared.domain.valueobjects;

import java.util.UUID;

/**
 * Marker interface for aggregate identity value objects backed by a {@link UUID}.
 *
 * <p>All implementations are immutable records with a single {@code value} component. Use the
 * static factory methods {@code of(String)}, {@code of(UUID)} and {@code generate()} defined on
 * each concrete record to obtain instances.
 */
public interface Identifier {

  UUID value();
}
