package com.asia.booklender.auth.service.impl;

import com.asia.booklender.auth.dto.AuthRequest;
import com.asia.booklender.auth.dto.AuthResponse;
import com.asia.booklender.auth.service.AuthService;
import com.asia.booklender.auth.util.JwtUtil;
import com.asia.booklender.member.entity.Member;
import com.asia.booklender.member.repository.MemberRepository;
import com.asia.booklender.shared.security.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of {@link AuthService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    // Injected from the SecurityConfig
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticates the user and generates a JWT token.
     *
     * <p>
     * Authentication is delegated to Spring Security.
     * If authentication succeeds, a JWT token is generated and returned to the client.
     * </p>
     *
     * @param request the authentication request containing email and password
     * @return an {@link AuthResponse} containing JWT token and user details
     *
     */
    @Override
    public AuthResponse authenticate(AuthRequest request) {
        // Delegate authentication to Spring Security
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
                ));

        log.debug("Authentication successful for email={}", request.getEmail());

        // Load authenticated user details
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        Objects.requireNonNull(userDetails, "UserDetails must not be null");

        // Generate JWT token for the authenticated user
        final String jwtToken = jwtUtil.generateToken(userDetails);

        return buildAuthResponse(userDetails, jwtToken);
    }

    /**
     * Builds the authentication response returned to the client.
     *
     * <p>
     * If the authenticated user has a MEMBER role, the corresponding member ID is also included in the response.
     * </p>
     *
     * @param userDetails the authenticated user's details
     * @param jwt the generated JWT token
     * @return a populated {@link AuthResponse}
     */
    private AuthResponse buildAuthResponse(UserDetails userDetails, String jwt) {
        String authority = getAuthority(userDetails);
        AuthResponse authResponse = AuthResponse
                .builder()
                .token(jwt)
                .email(userDetails.getUsername())
                .authority(authority)
                .build();

        // Include memberId only for MEMBER role
        if (authority.equals(Role.MEMBER.getAuthority())) {
            authResponse.setMemberId(getMemberId(userDetails.getUsername()));
        }

        return authResponse;
    }


    /**
     * Retrieves the member ID associated with the given email.
     *
     * @param userEmail the email of the authenticated user
     * @return the member ID if found, otherwise {@code null}
     */
    private Long getMemberId(String userEmail) {
        Optional<Member> member = memberRepository.findByEmail(userEmail);
        return member.map(Member::getId).orElse(null);
    }

    /**
     * Extracts the user's primary authority (role).
     *
     * @param userDetails the authenticated user's details
     * @return the granted authority as a string
     *
     * @throws IllegalStateException if the user has no assigned roles
     */
    private String getAuthority(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User has no roles"));
    }
}
