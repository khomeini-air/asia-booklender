package com.asia.booklender.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

public class SecurityUtil {

    /**
     * Retrieves the currently authenticated user and its roles from Spring Security context.
     *
     * @return {@link CurrentUser} current user
     */
    public static CurrentUser currentUser() {
        // Retrieve Authentication from the Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // If authenticated, retrieve the username and roles
        String username = authentication.getName(); // email

        Set<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new CurrentUser(username, roles);
    }
}
