package com.risetogether.authentication.domain.aggregate;

import com.risetogether.authentication.domain.valueobjects.TermsDocumentId;
import com.risetogether.authentication.domain.valueobjects.TermsType;
import com.risetogether.shared.domain.exceptions.ValidationException;
import com.risetogether.shared.domain.valueobjects.Timestamp;

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

  /**
   * Publishes a new legal document.
   *
   * @param termsType the category of the document; must not be {@code null}
   * @param version   a human-readable version identifier (e.g. {@code "1.2"}); must not be blank
   * @param content   the full text of the document; must not be blank
   * @return a new {@code TermsDocument} with a generated id and {@code publishedAt = now()}
   * @throws com.risetogether.shared.domain.exceptions.ValidationException if any argument is invalid
   */
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

  /**
   * Rebuilds a {@code TermsDocument} from persisted data without side effects.
   *
   * <p>Intended for use exclusively by persistence mappers.
   */
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
