package com.risetogether.authentication.infrastructure.adapters.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.risetogether.authentication.application.ports.out.LoadTermsDocumentPort;
import com.risetogether.authentication.application.ports.out.SaveTermsAcceptancePort;
import com.risetogether.authentication.domain.aggregate.TermsDocument;
import com.risetogether.authentication.domain.entity.TermsAcceptance;
import com.risetogether.authentication.domain.valueobjects.TermsType;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.SpringDataJpaTermsAcceptanceRepository;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.SpringDataJpaTermsDocumentRepository;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.mappers.write.TermsAcceptanceMapper;
import com.risetogether.authentication.infrastructure.adapters.out.persistence.jpa.mappers.write.TermsDocumentMapper;

@Component
public class JpaTermsRepositoryAdapter implements LoadTermsDocumentPort, SaveTermsAcceptancePort {

  private final SpringDataJpaTermsDocumentRepository termsDocumentRepo;
  private final SpringDataJpaTermsAcceptanceRepository termsAcceptanceRepo;
  private final TermsDocumentMapper termsDocumentMapper;
  private final TermsAcceptanceMapper termsAcceptanceMapper;

  public JpaTermsRepositoryAdapter(
      SpringDataJpaTermsDocumentRepository termsDocumentRepo,
      SpringDataJpaTermsAcceptanceRepository termsAcceptanceRepo,
      TermsDocumentMapper termsDocumentMapper,
      TermsAcceptanceMapper termsAcceptanceMapper) {
    this.termsDocumentRepo = termsDocumentRepo;
    this.termsAcceptanceRepo = termsAcceptanceRepo;
    this.termsDocumentMapper = termsDocumentMapper;
    this.termsAcceptanceMapper = termsAcceptanceMapper;
  }

  @Override
  public Optional<TermsDocument> findLatestByType(TermsType type) {
    return termsDocumentRepo.findLatestByTermsType(type).map(termsDocumentMapper::toDomain);
  }

  @Override
  public void save(TermsAcceptance acceptance) {
    termsAcceptanceRepo.save(termsAcceptanceMapper.toEntity(acceptance));
  }
}
