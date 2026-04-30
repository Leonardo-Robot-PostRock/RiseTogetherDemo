package com.risetogether.profiles.domain.aggregate;

import com.risetogether.shared.domain.AggregateRoot;
import com.risetogether.shared.domain.exceptions.ValidationException;
import com.risetogether.shared.domain.valueobjects.EmployerId;
import com.risetogether.shared.domain.valueobjects.Timestamp;
import com.risetogether.shared.domain.valueobjects.UserId;

/**
 * Aggregate root for the {@code profiles} bounded context representing an employer (company)
 * profile on the platform.
 *
 * <p>An {@code EmployerAggregate} is associated with exactly one {@link UserId} (the platform
 * user who owns the company profile) and holds all public-facing company information.
 *
 * <h2>Factory methods</h2>
 * <ul>
 *   <li>{@link #create} — creates a new employer profile with required fields</li>
 *   <li>{@link #reconstitute} — rebuilds from persistence without side effects</li>
 * </ul>
 *
 * <h2>Mutating methods</h2>
 * <ul>
 *   <li>{@link #updateProfile} — updates core company info</li>
 *   <li>{@link #updateLogo} — replaces the logo URL</li>
 *   <li>{@link #updateDescription} — updates the company description</li>
 *   <li>{@link #updateCompanySize} — updates the headcount band</li>
 * </ul>
 */
public class EmployerAggregate extends AggregateRoot {
  private final EmployerId id;
  private final UserId userId;
  private final Timestamp createdAt;
  private String companyName;
  private String industry;
  private String website;
  private String location;
  private String contactPerson;
  private String contactEmail;
  private String logoUrl;
  private String description;
  private String companySize;
  private Timestamp updatedAt;

  private EmployerAggregate(
      EmployerId id,
      UserId userId,
      String companyName,
      String industry,
      String website,
      String location,
      String contactPerson,
      String contactEmail,
      Timestamp createdAt) {
    this.id = id;
    this.userId = userId;
    this.companyName = companyName;
    this.industry = industry;
    this.website = website;
    this.location = location;
    this.contactPerson = contactPerson;
    this.contactEmail = contactEmail;
    this.createdAt = createdAt;
    this.updatedAt = createdAt;
  }

  /**
   * Creates a new employer profile.
   *
   * @param userId        the platform user who owns this profile; must not be {@code null}
   * @param companyName   the legal company name; must not be blank
   * @param industry      optional industry sector (e.g. {@code "Technology"})
   * @param website       optional company website URL
   * @param location      optional primary office location
   * @param contactPerson optional name of the primary contact
   * @param contactEmail  optional email for job-seeker enquiries
   * @return a new {@code EmployerAggregate} ready to be persisted
   * @throws ValidationException if {@code companyName} is blank
   */
  public static EmployerAggregate create(
      UserId userId,
      String companyName,
      String industry,
      String website,
      String location,
      String contactPerson,
      String contactEmail) {
    if (companyName == null || companyName.isBlank()) {
      throw new ValidationException("Company name cannot be empty");
    }

    return new EmployerAggregate(
        EmployerId.generate(),
        userId,
        companyName,
        industry,
        website,
        location,
        contactPerson,
        contactEmail,
        Timestamp.now());
  }

  /**
   * Rebuilds an {@code EmployerAggregate} from persisted data without triggering any domain
   * logic or recording domain events.
   *
   * <p>Intended for use exclusively by persistence mappers.
   *
   * @param id            stored employer id
   * @param userId        associated platform user id
   * @param companyName   stored company name
   * @param industry      stored industry
   * @param website       stored website
   * @param location      stored location
   * @param contactPerson stored contact person name
   * @param contactEmail  stored contact email
   * @param logoUrl       stored logo URL (may be {@code null})
   * @param description   stored company description (may be {@code null})
   * @param companySize   stored company size band (may be {@code null})
   * @param createdAt     stored creation timestamp
   * @param updatedAt     stored last-updated timestamp
   * @return a reconstituted {@code EmployerAggregate}
   */
  public static EmployerAggregate reconstitute(
      EmployerId id,
      UserId userId,
      String companyName,
      String industry,
      String website,
      String location,
      String contactPerson,
      String contactEmail,
      String logoUrl,
      String description,
      String companySize,
      Timestamp createdAt,
      Timestamp updatedAt) {
    EmployerAggregate employer =
        new EmployerAggregate(
            id,
            userId,
            companyName,
            industry,
            website,
            location,
            contactPerson,
            contactEmail,
            createdAt);
    employer.logoUrl = logoUrl;
    employer.description = description;
    employer.companySize = companySize;
    employer.updatedAt = updatedAt;
    return employer;
  }

  /**
   * Updates the core company profile fields. A {@code null} or blank {@code companyName}
   * is ignored (the existing name is preserved).
   *
   * @param companyName   new company name (optional update)
   * @param industry      new industry sector
   * @param website       new website URL
   * @param location      new primary location
   * @param contactPerson new contact person name
   * @param contactEmail  new contact email
   */
  public void updateProfile(
      String companyName,
      String industry,
      String website,
      String location,
      String contactPerson,
      String contactEmail) {
    if (companyName != null && !companyName.isBlank()) {
      this.companyName = companyName;
    }
    this.industry = industry;
    this.website = website;
    this.location = location;
    this.contactPerson = contactPerson;
    this.contactEmail = contactEmail;
    this.updatedAt = Timestamp.now();
  }

  /**
   * Replaces the company logo URL.
   *
   * @param logoUrl the new logo URL (may be {@code null} to remove)
   */
  public void updateLogo(String logoUrl) {
    this.logoUrl = logoUrl;
    this.updatedAt = Timestamp.now();
  }

  /**
   * Updates the company description shown on the public profile.
   *
   * @param description the new description text
   */
  public void updateDescription(String description) {
    this.description = description;
    this.updatedAt = Timestamp.now();
  }

  /**
   * Updates the company size band (e.g. {@code "1-10"}, {@code "51-200"}).
   *
   * @param companySize the new size band string
   */
  public void updateCompanySize(String companySize) {
    this.companySize = companySize;
    this.updatedAt = Timestamp.now();
  }

  public EmployerId getId() { return id; }
  public UserId getUserId() { return userId; }
  public String getCompanyName() { return companyName; }
  public String getIndustry() { return industry; }
  public String getWebsite() { return website; }
  public String getLocation() { return location; }
  public String getContactPerson() { return contactPerson; }
  public String getContactEmail() { return contactEmail; }
  public String getLogoUrl() { return logoUrl; }
  public String getDescription() { return description; }
  public String getCompanySize() { return companySize; }
  public Timestamp getCreatedAt() { return createdAt; }
  public Timestamp getUpdatedAt() { return updatedAt; }
}
