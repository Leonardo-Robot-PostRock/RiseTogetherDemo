package com.ITJobsBackend.authentication.application.ports.out;

import java.util.List;

public interface TokenGeneratorPort {
    String generateAccessToken(String userId, List<String> roles);
    String generateRefreshToken(String userId);
    boolean validateToken(String token);
    String extractUserId(String token);
}
