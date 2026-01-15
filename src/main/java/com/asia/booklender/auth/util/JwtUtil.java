package com.asia.booklender.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Utility class for handling JWT operations:
 * <li>Token generation</li>
 * <li>Token validation</li>
 * <li>Claim extraction</li>
 *
 * <p>
 * This class is stateless and relies on configuration properties
 * for signing secret and expiration.
 * </p>
 */
@Component
@Slf4j
public class JwtUtil {

    /**
     * Secret key to sign and verify the JWT token
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token expiration time in milliseconds.
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Extracts username (subject) from JWT token.
     *
     * @param token JWT token
     * @return username (email in this system)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    /**
     * Extracts expiration date from JWT token.
     *
     * @param token JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from the token.
     *
     * @param token          JWT token
     * @param claimsResolver function mapping Claims -> desired value
     * @param <T>            return type
     * @return extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a JWT token for the authenticated user.
     * Token contains:
     * <li>subject: username (email)</li>
     * <li>claim: role</li>
     *
     * @param userDetails authenticated user
     * @return signed JWT token
     */
    public String generateToken(UserDetails userDetails) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
            String token = createToken(claims, userDetails.getUsername());
            log.debug("JWT generated for user={}", userDetails.getUsername());

            return token;
        } catch (Exception e) {
            log.error("JWT generation failed for user={}", userDetails.getUsername(), e);
            throw e;
        }
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates JWT token against: username match and expiration
     * <p>
     * Note: Signature validation already happens during parsing
     * </p>
     *
     * @param token        JWT token
     * @param userDetails expected user
     * @return true if valid
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
