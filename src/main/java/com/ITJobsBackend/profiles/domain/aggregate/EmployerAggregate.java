package com.ITJobsBackend.profiles.domain.aggregate;

import com.ITJobsBackend.shared.domain.AggregateRoot;
import com.ITJobsBackend.shared.domain.exceptions.ValidationException;
import com.ITJobsBackend.shared.domain.valueobjects.EmployerId;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

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

  public void updateLogo(String logoUrl) {
    this.logoUrl = logoUrl;
    this.updatedAt = Timestamp.now();
  }

  public void updateDescription(String description) {
    this.description = description;
    this.updatedAt = Timestamp.now();
  }

  public void updateCompanySize(String companySize) {
    this.companySize = companySize;
    this.updatedAt = Timestamp.now();
  }

  public EmployerId getId() {
    return id;
  }

  public UserId getUserId() {
    return userId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getIndustry() {
    return industry;
  }

  public String getWebsite() {
    return website;
  }

  public String getLocation() {
    return location;
  }

  public String getContactPerson() {
    return contactPerson;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public String getDescription() {
    return description;
  }

  public String getCompanySize() {
    return companySize;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }
}
