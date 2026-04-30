package com.risetogether.shared.domain.event;

import java.time.Instant;
import java.util.UUID;

import com.risetogether.shared.domain.valueobjects.Identifier;

/**
 * Base class for all domain events in the system.
 *
 * <p>A domain event represents something meaningful that happened within a bounded context.
 * Each event is immutable and carries the minimum amount of data needed by consumers: the
 * identity of the aggregate that originated it, its type, and the moment it occurred.
 *
 * <p>Concrete events should extend this class and add any payload fields relevant to the
 * specific occurrence (e.g. the user's email when a {@code UserRegisteredEvent} is fired).
 *
 * <p>Usage example:
 * <pre>{@code
 * public class UserRegisteredEvent extends DomainEvent {
 *     private final String email;
 *     public UserRegisteredEvent(UserId userId, Email email) {
 *         super(userId, "User", UserEventTypes.USER_REGISTERED);
 *         this.email = email.value();
 *     }
 * }
 * }</pre>
 */
public abstract class DomainEvent {
  private final String eventId;
  private final String aggregateId;
  private final String aggregateType;
  private final String eventType;
  private final Instant occurredOn;

  /**
   * Creates a new domain event.
   *
   * @param aggregateId   the identity of the aggregate root that raised this event
   * @param aggregateType a human-readable name for the aggregate type (e.g. {@code "User"})
   * @param eventName     a unique string identifying the event type (e.g. {@code "user.registered"})
   */
  protected DomainEvent(Identifier aggregateId, String aggregateType, String eventName) {
    this.eventId = UUID.randomUUID().toString();
    this.aggregateId = aggregateId.toString();
    this.aggregateType = aggregateType;
    this.eventType = eventName;
    this.occurredOn = Instant.now();
  }

  /** @return a unique identifier for this particular event instance */
  public String getEventId() {
    return eventId;
  }

  /** @return the string representation of the aggregate's {@link Identifier} */
  public String getAggregateId() {
    return aggregateId;
  }

  /** @return the event type discriminator (e.g. {@code "user.registered"}) */
  public String getEventType() {
    return eventType;
  }

  /** @return the aggregate type name (e.g. {@code "User"}) */
  public String getAggregateType() {
    return aggregateType;
  }

  /** @return the UTC instant at which this event occurred */
  public Instant getOccurredOn() {
    return occurredOn;
  }

  protected String baseFields() {
    return "aggregateId='"
        + aggregateId
        + '\''
        + ", aggregateType='"
        + aggregateType
        + '\''
        + ", eventType='"
        + eventType
        + '\''
        + ", occurredOn="
        + occurredOn;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" + baseFields() + '}';
  }
}
