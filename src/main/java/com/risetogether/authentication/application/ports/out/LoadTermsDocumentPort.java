package com.risetogether.authentication.application.ports.out;

import java.util.Optional;

import com.risetogether.authentication.domain.aggregate.TermsDocument;
import com.risetogether.authentication.domain.valueobjects.TermsType;

public interface LoadTermsDocumentPort {

  Optional<TermsDocument> findLatestByType(TermsType type);
}
