package com.ITJobsBackend.authentication.domain.aggregate;

import com.ITJobsBackend.authentication.domain.valueobjects.TermsDocumentId;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsType;
import com.ITJobsBackend.shared.domain.exceptions.ValidationException;
import com.ITJobsBackend.shared.domain.valueobjects.Timestamp;

/**
 * Catalog entry for a published version of a legal document (e.g. Terms of Service). Immutable once
 * created — new versions are new aggregate instances.
 */
public class TermsDocument {

  private final TermsDocumentId id;
  private final TermsType termsType;
  private final String version;
  private final String content;
  private final Timestamp publishedAt;

  private TermsDocument(
      TermsDocumentId id,
      TermsType termsType,
      String version,
      String content,
      Timestamp publishedAt) {
    this.id = id;
    this.termsType = termsType;
    this.version = version;
    this.content = content;
    this.publishedAt = publishedAt;
  }

  public static TermsDocument create(TermsType termsType, String version, String content) {
    if (termsType == null) {
      throw new ValidationException("Terms type cannot be null");
    }

    if (version == null || version.isBlank()) {
      throw new ValidationException("Version cannot be blank");
    }

    if (content == null || content.isBlank()) {
      throw new ValidationException("Content cannot be blank");
    }

    return new TermsDocument(
        TermsDocumentId.generate(), termsType, version, content, Timestamp.now());
  }

  public static TermsDocument reconstitute(
      TermsDocumentId id,
      TermsType termsType,
      String version,
      String content,
      Timestamp publishedAt) {
    return new TermsDocument(id, termsType, version, content, publishedAt);
  }

  public TermsDocumentId getId() {
    return id;
  }

  public TermsType getTermsType() {
    return termsType;
  }

  public String getVersion() {
    return version;
  }

  public String getContent() {
    return content;
  }

  public Timestamp getPublishedAt() {
    return publishedAt;
  }
}
