package com.asia.booklender.auth.filter;

import com.asia.booklender.auth.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Extracts and validates JWT from Authorization header.
     * <p>
     * If valid, populates Spring SecurityContext with authenticated user.
     * If invalid, request is short-circuited with HTTP 401.
     * </p>
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        // No Bearer token found - continue without authentication and let Spring Security decide whether the endpoint requires authentication.
        // This filter only attempts authentication if the credentials are present.
        // It also allows public endpoints to be accessible.
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        // Authenticate the user against the provided token
        try {
            authenticate(token, request);
        } catch (JwtException ex) {
            // Invalid or expired token - reject request
            handleInvalidToken(response, ex);
            return;
        }

        // Authentication successful - continue the filter chain
        chain.doFilter(request, response);
    }

    /**
     * Extracts JWT from Authorization header.
     *
     * @param request {@link HttpServletRequest}
     */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }

    /**
     * Authenticates user and populates SecurityContext.
     *
     * @param token user's token to authenticate.
     * @param request {@link HttpServletRequest}
     */
    private void authenticate(String token, HttpServletRequest request) {
        String username = jwtUtil.extractUsername(token);

        // Skip authentication if username is missing or already authenticated.
        // Some endpoints are public and Spring Security decides access later.
        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Validate JWT integrity and expiration
        if (!jwtUtil.validateToken(token, userDetails)) {
            throw new JwtException("Token validation failed");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // password is not stored
                        userDetails.getAuthorities()
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Store authentication in SecurityContext for downstream access.
        // This enables @preauthorize, hasRole, and access in services.
        // SecurityContextHolder is request-scoped in stateless apps, hence no session is created
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Handles invalid JWT by returning 401.
     *
     * @param response {@link HttpServletResponse}
     * @param ex {@link JwtException}
     */
    private void handleInvalidToken(HttpServletResponse response, JwtException ex) throws IOException {
        log.warn("Invalid JWT token: {}", ex.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * Skip JWT authentication for public endpoints.
     * It's functionally overlaps with Security Config, but it's not a redundant since both operates on different layers
     * @param request {@link HttpServletRequest}
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth")
                || path.startsWith("/actuator")
                || path.startsWith("/api/books");
    }
}
