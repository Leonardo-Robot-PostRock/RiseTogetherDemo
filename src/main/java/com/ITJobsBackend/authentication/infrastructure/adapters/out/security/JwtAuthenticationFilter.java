package com.ITJobsBackend.authentication.infrastructure.adapters.out.security;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ITJobsBackend.authentication.application.ports.out.TokenGeneratorPort;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenGeneratorPort tokenGenerator;

    public JwtAuthenticationFilter(TokenGeneratorPort tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    /**
     * This method is called for every incoming HTTP request. It checks for the
     * presence of a JWT token in the Authorization header,
     * validates it, and if valid, allows the request to proceed. If the token is
     * missing or invalid, it simply passes the request
     * along the filter chain without setting any authentication context.
     *
     * @param request     The HttpServletRequest object containing client request
     *                    data
     * @param response    The HttpServletResponse object for sending data back to
     *                    the client
     * @param filterChain The FilterChain object that allows the filter to pass the
     *                    request and response to the next entity in the filter
     *                    chain
     * @throws ServletException If an error occurs during the filtering process
     * @throws IOException      If an I/O error occurs during the filtering process
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, // Request object containing client request data
            @NonNull HttpServletResponse response, // Response object for sending data back to the client
            @NonNull FilterChain filterChain // Object that allows the filter to pass the request and response to the next
                                             // entity in the filter chain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // Validate the token
        if (!tokenGenerator.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = tokenGenerator.extractUserId(token);
        List<String> roles = tokenGenerator.extractRoles(token);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null,
                authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/");
    }

}
