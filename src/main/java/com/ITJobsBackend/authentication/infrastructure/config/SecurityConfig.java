package com.ITJobsBackend.authentication.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ITJobsBackend.authentication.infrastructure.adapters.out.security.JwtAuthenticationFilter;
import com.ITJobsBackend.shared.infrastructure.exceptions.SecurityExceptionHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final SecurityExceptionHandler securityExceptionHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  SecurityConfig(
      JwtAuthenticationFilter jwtAuthenticationFilter,
      SecurityExceptionHandler securityExceptionHandler) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.securityExceptionHandler = securityExceptionHandler;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(securityExceptionHandler)
                    .accessDeniedHandler(securityExceptionHandler))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/v1/auth/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/jobs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /*
   * Since we're using JWT, we don't need to load user details from the database
   * for authentication
   * Our API is stateless, the user details service interface core is for stateful
   * authentication,
   * so we can throw an exception if it's called, indicating that JWT
   * authentication should be used instead.
   */
  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      throw new UsernameNotFoundException("Use JWT authentication");
    };
  }
}
