package com.ITJobsBackend.authentication.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ITJobsBackend.authentication.domain.service.CredentialsVerifier;

@Configuration
public class DomainServiceConfig {

  @Bean
  public CredentialsVerifier credentialsVerifier() {
    return new CredentialsVerifier();
  }
}
