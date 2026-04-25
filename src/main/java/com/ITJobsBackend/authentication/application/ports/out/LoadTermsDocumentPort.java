package com.ITJobsBackend.authentication.application.ports.out;

import java.util.Optional;

import com.ITJobsBackend.authentication.domain.aggregate.TermsDocument;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsType;

public interface LoadTermsDocumentPort {

  Optional<TermsDocument> findLatestByType(TermsType type);
}
