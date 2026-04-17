package com.ITJobsBackend.authentication.domain.repository;

import java.util.Optional;

import com.ITJobsBackend.authentication.domain.aggregate.TermsDocument;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsType;

public interface TermsDocumentReaderRepository {

    /** Returns the most recently published document for the given type. */
    Optional<TermsDocument> findLatestByType(TermsType type);
}

