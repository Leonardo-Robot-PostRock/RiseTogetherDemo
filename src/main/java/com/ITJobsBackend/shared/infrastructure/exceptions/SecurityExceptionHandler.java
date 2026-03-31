package com.ITJobsBackend.shared.infrastructure.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
  private static final Logger log = LoggerFactory.getLogger(SecurityExceptionHandler.class);
  private final ObjectMapper objectMapper;

  public SecurityExceptionHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    log.warn(
        "Authentication failed - IP: {}, URI: {}, reason: {}",
        request.getRemoteAddr(),
        request.getRequestURI(),
        authException.getMessage());

    writeErrorResponse(
        response, HttpStatus.UNAUTHORIZED, "Unauthorized", authException.getMessage());
  }

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    log.warn(
        "Access denied - IP: {}, URI: {}, reason: {}",
        request.getRemoteAddr(),
        request.getRequestURI(),
        accessDeniedException.getMessage());

    writeErrorResponse(response, HttpStatus.FORBIDDEN, "Forbidden", "Access denied");
  }

  private void writeErrorResponse(
      HttpServletResponse response, HttpStatus status, String error, String message)
      throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    ErrorResponse errorResponse = new ErrorResponse(status.value(), error, message, Instant.now());

    objectMapper.writeValue(response.getOutputStream(), errorResponse);
  }

  record ErrorResponse(int status, String error, String message, Instant timestamp) {}
}
