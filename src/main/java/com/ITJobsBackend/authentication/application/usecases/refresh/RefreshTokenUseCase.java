package com.ITJobsBackend.authentication.application.usecases.refresh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ITJobsBackend.authentication.application.ports.in.RefreshTokenPort;
import com.ITJobsBackend.authentication.application.ports.out.LoadUserPort;
import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;
import com.ITJobsBackend.authentication.application.usecases.login.AuthTokenResponse;
import com.ITJobsBackend.authentication.domain.aggregate.UserAggregate;
import com.ITJobsBackend.shared.domain.valueobjects.UserId;

@Service
@Transactional
public class RefreshTokenUseCase implements RefreshTokenPort {
  private static final Logger log = LoggerFactory.getLogger(RefreshTokenUseCase.class);

  private final TokenGeneratorPort tokenGenerator;
  private final LoadUserPort loadUserPort;

  public RefreshTokenUseCase(TokenGeneratorPort tokenGenerator, LoadUserPort loadUserPort) {
    this.tokenGenerator = tokenGenerator;
    this.loadUserPort = loadUserPort;
  }

  @Override
  public AuthTokenResponse execute(String refreshToken) {
    log.info("Refreshing access token");

    String userId = tokenGenerator.extractUserId(refreshToken);
    UserAggregate user =
        loadUserPort
            .findById(UserId.of(userId))
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    String newAccessToken = tokenGenerator.generateAccessToken(userId, user.getRoles());
    String newRefreshToken = tokenGenerator.generateRefreshToken(userId);

    log.info("Token refreshed successfully for user: {}", userId);

    return new AuthTokenResponse(
        userId,
        user.getUsername().value(),
        user.getEmail().value(),
        newAccessToken,
        newRefreshToken);
  }
}
