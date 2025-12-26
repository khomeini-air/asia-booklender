package com.asia.booklender.shared.observability.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    /**
     *  Logs basic request/response information and enriches logs with MDC data:
     *      <li> traceId : unique ID per request</li>
     *      <li>user    : authenticated username (if available)</li>
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Add traceId and authenticated user if available into the MDC
        putMDC();

        long start = System.currentTimeMillis();

        try {
            // Continue the filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Log request summary after processing
            long duration = System.currentTimeMillis() - start;

            log.info(
                    "request completed method={} path={} status={} durationMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration
            );

            // Clear the MDC after processing
            MDC.clear();
        }
    }

    private void putMDC() {
        // Add traceId
        MDC.put("traceId", UUID.randomUUID().toString());

        // Add authenticated user if available
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                MDC.put("user", userDetails.getUsername());
            } else if (principal instanceof String username) {
                MDC.put("user", username);
            }
        }
    }
}
