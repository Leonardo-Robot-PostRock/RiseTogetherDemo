package com.ITJobsBackend.authentication.infrastructure.adapters.out.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;

@Component
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {
  private final SecretKey secretKey;
  private final long accessTokenValidity;
  private final long refreshTokenValidity;

  public JwtTokenGeneratorAdapter(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-validity:900000}") long accessTokenValidity,
      @Value("${jwt.refresh-token-validity:604800000}") long refreshTokenValidity) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenValidity = accessTokenValidity;
    this.refreshTokenValidity = refreshTokenValidity;
  }

  @Override
  public String generateAccessToken(String userId, List<String> roles) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + accessTokenValidity);

    return Jwts.builder()
        .subject(userId)
        .claim("roles", roles)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(secretKey)
        .compact();
  }

  @Override
  public String generateRefreshToken(String userId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshTokenValidity);

    return Jwts.builder()
        .subject(userId)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(secretKey)
        .compact();
  }

  @Override
  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public String extractUserId(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  @Override
  public List<String> extractRoles(String token) {
    Object roles =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("roles");

    if (roles instanceof List<?> list) {
      return list.stream().map(String::valueOf).toList();
    }
    return List.of();
  }
}
