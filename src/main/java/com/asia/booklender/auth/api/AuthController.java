package com.asia.booklender.auth.api;

import com.asia.booklender.auth.dto.AuthRequest;
import com.asia.booklender.auth.dto.AuthResponse;
import com.asia.booklender.auth.service.AuthService;
import com.asia.booklender.shared.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {
    private final AuthService authService;

    /**
     * Authenticates a user using email and password.
     *
     * @param request the authentication request containing user email and password
     * @return {@link AuthResponse} (JWT token and user information) if authentication succeeds
     *
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid AuthRequest request) {
        log.info("Login attempt received for email={}", request.getEmail());
        AuthResponse authResponse = authService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.success(authResponse));
    }
}
