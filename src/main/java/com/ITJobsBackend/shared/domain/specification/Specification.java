package com.ITJobsBackend.shared.domain.specification;

/**
 * Generic Specification pattern for domain filtering.
 *
 * <p>Encapsulates a single filtering criterion over a candidate of type {@code T}. Specifications
 * can be composed with {@link #and(Specification)} and {@link #or(Specification)} to build complex
 * predicate trees in a type-safe, reusable way.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * Specification<JobAggregate> spec =
 *     new TitleContainsSpecification("java")
 *         .and(new JobStatusSpecification(JobStatus.OPEN));
 * List<JobAggregate> results = jobRepo.findAll(spec);
 * }</pre>
 *
 * @param <T> the type of domain object being evaluated
 */
@FunctionalInterface
public interface Specification<T> {

  /**
   * Evaluates whether the candidate satisfies this specification.
   *
   * @param candidate the object to evaluate; must not be {@code null}
   * @return {@code true} if the candidate matches this criterion
   */
  boolean isSatisfiedBy(T candidate);

  /**
   * Returns a composed specification satisfied only when both {@code this} and {@code other} are
   * satisfied (logical AND).
   *
   * @param other the second specification; must not be {@code null}
   * @return a new AND-composed {@code Specification<T>}
   */
  default Specification<T> and(Specification<T> other) {
    return candidate -> this.isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate);
  }

  /**
   * Returns a composed specification satisfied when either {@code this} or {@code other} is
   * satisfied (logical OR).
   *
   * @param other the second specification; must not be {@code null}
   * @return a new OR-composed {@code Specification<T>}
   */
  default Specification<T> or(Specification<T> other) {
    return candidate -> this.isSatisfiedBy(candidate) || other.isSatisfiedBy(candidate);
  }
}
