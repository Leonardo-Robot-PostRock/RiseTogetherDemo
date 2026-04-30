package com.risetogether.authentication.application.usecases.refresh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.risetogether.authentication.application.ports.in.RefreshTokenPort;
import com.risetogether.authentication.application.ports.out.QueryUserPort;
import com.risetogether.authentication.application.ports.out.TokenGeneratorPort;
import com.risetogether.authentication.application.query.UserView;
import com.risetogether.authentication.application.usecases.login.AuthTokenResponse;
import com.risetogether.shared.domain.valueobjects.UserId;

@Service
@Transactional(readOnly = true)
public class RefreshTokenUseCase implements RefreshTokenPort {
  private static final Logger log = LoggerFactory.getLogger(RefreshTokenUseCase.class);

  private final TokenGeneratorPort tokenGenerator;
  private final QueryUserPort queryUserPort;

  public RefreshTokenUseCase(TokenGeneratorPort tokenGenerator, QueryUserPort queryUserPort) {
    this.tokenGenerator = tokenGenerator;
    this.queryUserPort = queryUserPort;
  }

  @Override
  public AuthTokenResponse execute(String refreshToken) {
    log.info("Refreshing access token");

    String userId = tokenGenerator.extractUserId(refreshToken);
    UserView user =
        queryUserPort
            .findById(UserId.of(userId))
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    String newAccessToken = tokenGenerator.generateAccessToken(userId, user.roles());
    String newRefreshToken = tokenGenerator.generateRefreshToken(userId);

    log.info("Token refreshed successfully for user: {}", userId);

    return new AuthTokenResponse(
        userId,
        user.username(),
        user.email(),
        newAccessToken,
        newRefreshToken);
  }
}
