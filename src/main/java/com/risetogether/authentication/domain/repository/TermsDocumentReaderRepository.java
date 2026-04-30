package com.risetogether.authentication.domain.repository;

import java.util.Optional;

import com.risetogether.authentication.domain.aggregate.TermsDocument;
import com.risetogether.authentication.domain.valueobjects.TermsType;

public interface TermsDocumentReaderRepository {

  /** Returns the most recently published document for the given type. */
  Optional<TermsDocument> findLatestByType(TermsType type);
}
