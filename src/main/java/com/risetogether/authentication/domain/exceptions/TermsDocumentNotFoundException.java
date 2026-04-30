package com.risetogether.authentication.domain.exceptions;

import com.risetogether.shared.domain.exceptions.NotFoundException;

/**
 * Thrown when no published {@link com.risetogether.authentication.domain.aggregate.TermsDocument}
 * can be found for a given {@link com.risetogether.authentication.domain.valueobjects.TermsType}.
 *
 * <p>Mapped to HTTP {@code 404 Not Found} by the {@code GlobalExceptionHandler}.
 */
public class TermsDocumentNotFoundException extends NotFoundException {

  /**
   * @param termsType the type of terms document that was not found (e.g. {@code "TERMS_OF_SERVICE"})
   */
  public TermsDocumentNotFoundException(String termsType) {
    super("No published terms document found for type: " + termsType);
  }
}
