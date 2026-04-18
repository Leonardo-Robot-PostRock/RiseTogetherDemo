package com.ITJobsBackend.authentication.domain.aggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ITJobsBackend.authentication.domain.event.EmailChangedEvent;
import com.ITJobsBackend.authentication.domain.event.EmailVerifiedEvent;
import com.ITJobsBackend.authentication.domain.event.GoogleAccountLinkedEvent;
import com.ITJobsBackend.authentication.domain.event.PasswordChangedEvent;
import com.ITJobsBackend.authentication.domain.event.UserActivatedEvent;
import com.ITJobsBackend.authentication.domain.event.UserDeactivatedEvent;
import com.ITJobsBackend.authentication.domain.exceptions.EmailAlreadyVerifiedException;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyActivatedException;
import com.ITJobsBackend.authentication.domain.exceptions.UserAlreadyDeactivatedException;
import com.ITJobsBackend.authentication.domain.valueobjects.GoogleSub;
import com.ITJobsBackend.authentication.domain.valueobjects.HashedPassword;
import com.ITJobsBackend.authentication.domain.valueobjects.UserStatus;
import com.ITJobsBackend.authentication.domain.valueobjects.Username;
import com.ITJobsBackend.authentication.domain.valueobjects.VerificationToken;
import com.ITJobsBackend.shared.domain.AggregateRoot;
import com.ITJobsBackend.shared.domain.valueobjects.Email;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

/**
 * Aggregate root for the {@code authentication} bounded context.
 *
 * <p>Represents a registered user and encapsulates all authentication-related state: credentials,
 * lifecycle status, optional Google OAuth linkage, email verification token, and assigned roles.
 *
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li>User is created via {@link #create} (status = {@code PENDING_VERIFICATION}) or via
 *       {@link #createGoogleUser} (status = {@code ACTIVE}, email already verified).</li>
 *   <li>A {@link VerificationToken} is assigned by the application layer and the user activates
 *       their account through {@link #verifyEmail()}, which fires {@link EmailVerifiedEvent}.</li>
 *   <li>An admin (or system process) may {@link #activate()} or {@link #deactivate()} the account.</li>
 * </ol>
 *
 * <h2>Factory methods</h2>
 * <ul>
 *   <li>{@link #create} — standard registration flow</li>
 *   <li>{@link #createGoogleUser} — Google OAuth sign-up</li>
 *   <li>{@link #reconstitute} — rebuild from persistence (no side effects / no events)</li>
 * </ul>
 *
 * <h2>Domain events raised</h2>
 * <ul>
 *   <li>{@link UserActivatedEvent} — from {@link #activate()}</li>
 *   <li>{@link UserDeactivatedEvent} — from {@link #deactivate()}</li>
 *   <li>{@link EmailVerifiedEvent} — from {@link #verifyEmail()}</li>
 *   <li>{@link PasswordChangedEvent} — from {@link #changePassword}</li>
 *   <li>{@link GoogleAccountLinkedEvent} — from {@link #linkGoogleAccount}</li>
 *   <li>{@link EmailChangedEvent} — from {@link #updateEmail}</li>
 * </ul>
 */
public class UserAggregate extends AggregateRoot {

  private final UserId id;
  private final Timestamp createdAt;
  private final List<String> roles;
  private Username username;
  private Email email;
  private HashedPassword password;
  private UserStatus status;
  private GoogleSub googleSub;
  private Timestamp updatedAt;
  private VerificationToken verificationToken;

  private UserAggregate(
      UserId id, Username username, Email email, HashedPassword password, Timestamp createdAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.status = UserStatus.PENDING_VERIFICATION;
    this.createdAt = createdAt;
    this.updatedAt = createdAt;
    this.roles = new ArrayList<>();
    this.roles.add("ROLE_USER");
  }

  /**
   * Creates a new user via the standard email/password registration flow.
   *
   * <p>The user starts with status {@code PENDING_VERIFICATION} and role {@code ROLE_USER}.
   * The {@code UserRegisteredEvent} is published by the use case, not here.
   *
   * @param username       validated display name
   * @param email          validated, normalised email address
   * @param hashedPassword bcrypt-encoded password
   * @return a new {@code UserAggregate} ready to be persisted
   */
  public static UserAggregate create(
      Username username, Email email, HashedPassword hashedPassword) {
    return new UserAggregate(UserId.generate(), username, email, hashedPassword, Timestamp.now());
  }

  /**
   * Creates a new user via Google OAuth sign-up.
   *
   * <p>The user starts with status {@code ACTIVE} because Google has already verified their email.
   *
   * @param username       display name derived from the Google profile
   * @param email          email from the Google ID token
   * @param hashedPassword a placeholder or empty hash (Google users typically have no local password)
   * @param googleSub      the stable {@code sub} claim from the Google ID token
   * @return a new active {@code UserAggregate}
   */
  public static UserAggregate createGoogleUser(
      Username username, Email email, HashedPassword hashedPassword, GoogleSub googleSub) {
    UserAggregate user =
        new UserAggregate(UserId.generate(), username, email, hashedPassword, Timestamp.now());
    user.googleSub = googleSub;
    user.status = UserStatus.ACTIVE;
    return user;
  }

  /**
   * Rebuilds a {@code UserAggregate} from persisted data without triggering any domain logic
   * or recording domain events.
   *
   * <p>Intended for use exclusively by persistence mappers.
   *
   * @param id                the stored user id
   * @param username          stored username
   * @param email             stored email
   * @param password          stored hashed password
   * @param active            value of the {@code active} column
   * @param emailVerified     value of the {@code email_verified} column
   * @param googleSub         stored google sub (may be {@code null})
   * @param createdAt         stored creation timestamp
   * @param updatedAt         stored last-updated timestamp
   * @param roles             stored role list
   * @param verificationToken stored verification token (may be {@code null})
   * @return a reconstituted {@code UserAggregate}
   */
  public static UserAggregate reconstitute(
      UserId id,
      Username username,
      Email email,
      HashedPassword password,
      boolean active,
      boolean emailVerified,
      GoogleSub googleSub,
      Timestamp createdAt,
      Timestamp updatedAt,
      List<String> roles,
      VerificationToken verificationToken) {

    UserAggregate user = new UserAggregate(id, username, email, password, createdAt);
    user.status = UserStatus.from(active, emailVerified);
    user.googleSub = googleSub;
    user.updatedAt = updatedAt;
    user.roles.clear();
    user.roles.addAll(roles);
    user.verificationToken = verificationToken;
    return user;
  }

  /**
   * Assigns a new email verification token to this user.
   *
   * @param verificationToken the token to assign
   * @throws EmailAlreadyVerifiedException if the email is already verified
   */
  public void assignVerificationToken(VerificationToken verificationToken) {
    if (this.status.isEmailVerified()) {
      throw new EmailAlreadyVerifiedException(
          "Cannot assign a verification token to an already verified email");
    }
    this.verificationToken = verificationToken;
    this.updatedAt = Timestamp.now();
  }

  /**
   * Activates the user account.
   *
   * <p>Fires {@link UserActivatedEvent}.
   *
   * @throws UserAlreadyActivatedException if the user is already {@code ACTIVE}
   */
  public void activate() {
    if (this.status == UserStatus.ACTIVE) {
      throw new UserAlreadyActivatedException("User is already active");
    }
    this.status = UserStatus.ACTIVE;
    this.updatedAt = Timestamp.now();
    recordEvent(new UserActivatedEvent(this.id, this.email));
  }

  /**
   * Deactivates (suspends) the user account.
   *
   * <p>Fires {@link UserDeactivatedEvent}.
   *
   * @throws UserAlreadyDeactivatedException if the user is not currently {@code ACTIVE}
   */
  public void deactivate() {
    if (this.status != UserStatus.ACTIVE) {
      throw new UserAlreadyDeactivatedException("User is already deactivated");
    }
    this.status = UserStatus.SUSPENDED;
    this.updatedAt = Timestamp.now();
    recordEvent(new UserDeactivatedEvent(this.id, this.email));
  }

  /**
   * Marks the email address as verified and sets the account to {@code ACTIVE}.
   *
   * <p>Fires {@link EmailVerifiedEvent}.
   *
   * @throws EmailAlreadyVerifiedException if the email was already verified
   */
  public void verifyEmail() {
    if (this.status.isEmailVerified()) {
      throw new EmailAlreadyVerifiedException("Email is already verified");
    }
    this.status = UserStatus.ACTIVE;
    this.updatedAt = Timestamp.now();
    recordEvent(new EmailVerifiedEvent(this.id, this.email));
  }

  /**
   * Replaces the current hashed password with a new one.
   *
   * <p>Fires {@link PasswordChangedEvent}.
   *
   * @param newPassword the new bcrypt-encoded password
   */
  public void changePassword(HashedPassword newPassword) {
    this.password = newPassword;
    this.updatedAt = Timestamp.now();
    recordEvent(new PasswordChangedEvent(this.id));
  }

  /**
   * Links an existing user to a Google account.
   *
   * <p>Fires {@link GoogleAccountLinkedEvent}.
   *
   * @param googleSub the Google {@code sub} claim to associate
   */
  public void linkGoogleAccount(GoogleSub googleSub) {
    this.googleSub = googleSub;
    this.updatedAt = Timestamp.now();
    recordEvent(new GoogleAccountLinkedEvent(this.id, googleSub));
  }

  /**
   * Updates the user's email address and resets the verification status.
   *
   * <p>Sets status back to {@code PENDING_VERIFICATION} and fires {@link EmailChangedEvent}.
   *
   * @param newEmail the new validated email address
   */
  public void updateEmail(Email newEmail) {
    this.email = newEmail;
    this.status = UserStatus.PENDING_VERIFICATION;
    this.updatedAt = Timestamp.now();
    recordEvent(new EmailChangedEvent(this.id, this.email));
  }

  /**
   * Adds a role to this user if not already present.
   *
   * <p>The role is normalised (trimmed and upper-cased) before being added.
   *
   * @param role the role string to add (e.g. {@code "ROLE_ADMIN"})
   */
  public void addRole(String role) {
    String normalized = role.trim().toUpperCase();
    if (!this.roles.contains(normalized)) {
      this.roles.add(normalized);
      this.updatedAt = Timestamp.now();
    }
  }

  public UserId getId() { return id; }
  public Username getUsername() { return username; }
  public Email getEmail() { return email; }
  public HashedPassword getPassword() { return password; }
  public UserStatus getStatus() { return status; }
  /** @return {@code true} if the account status is {@link UserStatus#ACTIVE} */
  public boolean isActive() { return status.isActive(); }
  /** @return {@code true} if the email has been verified (status is {@code ACTIVE} or {@code SUSPENDED}) */
  public boolean isEmailVerified() { return status.isEmailVerified(); }
  public GoogleSub getGoogleSub() { return googleSub; }
  public Timestamp getCreatedAt() { return createdAt; }
  public Timestamp getUpdatedAt() { return updatedAt; }
  /** @return an unmodifiable view of the user's roles */
  public List<String> getRoles() { return Collections.unmodifiableList(roles); }
  public VerificationToken getVerificationToken() { return verificationToken; }

  @Override
  public String toString() {
    return "UserAggregate{"
        + "id=" + id
        + ", username=" + username
        + ", email='" + email.mask() + '\''
        + ", status=" + status
        + ", roles=" + roles
        + '}';
  }
}
