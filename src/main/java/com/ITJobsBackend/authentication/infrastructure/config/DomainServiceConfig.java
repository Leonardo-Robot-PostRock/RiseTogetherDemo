package com.ITJobsBackend.authentication.infrastructure.config;

import com.ITJobsBackend.authentication.domain.service.CredentialsVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

  @Bean
  public CredentialsVerifier credentialsVerifier() {
    return new CredentialsVerifier();
  }
}
