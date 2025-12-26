package com.asia.booklender.auth.service;

import com.asia.booklender.auth.dto.AuthRequest;
import com.asia.booklender.auth.dto.AuthResponse;

/**
 * Service interface responsible for user authentication.
 *
 * <p>
 * Defines the contract for authenticating users using their credentials
 * and issuing JWT tokens upon successful authentication.
 * </p>
 */
public interface AuthService {
    /**
     * Authenticates a user using email and password.
     *
     * @param request the authentication request containing email and password
     * @return an {@link AuthResponse} containing a JWT token and user information
     *
     */
    AuthResponse authenticate(AuthRequest request);
}
