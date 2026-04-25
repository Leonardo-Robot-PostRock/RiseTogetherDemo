package com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.application.ports.out.LoadTermsDocumentPort;
import com.ITJobsBackend.authentication.application.ports.out.SaveTermsAcceptancePort;
import com.ITJobsBackend.authentication.domain.aggregate.TermsDocument;
import com.ITJobsBackend.authentication.domain.entity.TermsAcceptance;
import com.ITJobsBackend.authentication.domain.valueobjects.TermsType;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.SpringDataJpaTermsAcceptanceRepository;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.SpringDataJpaTermsDocumentRepository;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.mappers.write.TermsAcceptanceMapper;
import com.ITJobsBackend.authentication.infrastructure.adapters.out.persistence.jpa.mappers.write.TermsDocumentMapper;

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
