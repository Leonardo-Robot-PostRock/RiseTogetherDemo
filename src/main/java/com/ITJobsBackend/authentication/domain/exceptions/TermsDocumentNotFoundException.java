package com.ITJobsBackend.authentication.domain.exceptions;

import com.ITJobsBackend.shared.domain.exceptions.NotFoundException;

public class TermsDocumentNotFoundException extends NotFoundException {

  public TermsDocumentNotFoundException(String termsType) {
    super("No published terms document found for type: " + termsType);
  }
}
