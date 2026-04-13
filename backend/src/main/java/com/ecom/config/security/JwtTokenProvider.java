package com.ecom.config.security;

import com.ecom.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * T043 – JWT utility for generating and parsing signed tokens (Phase 2).
 *
 * <p>Uses HMAC-SHA256 with a symmetric secret configured via
 * {@code app.jwt.secret}. Tokens carry the user's UUID as the subject
 * and the role name as the {@code "role"} claim.
 *
 * <p>The secret must be at least 32 UTF-8 bytes (256 bits) for HS256.
 * Inject via {@code APP_JWT_SECRET} environment variable in production.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms:3600000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT for the given user.
     *
     * @param userId UUID string of the authenticated user
     * @param role   user's role, encoded as the {@code "role"} claim
     * @return compact, signed JWT string
     */
    public String generate(String userId, User.Role role) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);
        return Jwts.builder()
                .subject(userId)
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    /**
     * Parses and verifies the token, returning its {@link Claims}.
     *
     * @throws JwtException if the token is invalid or expired
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** Returns {@code true} if the token signature is valid and not expired. */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Extracts the expiry {@link Instant} from a valid token. */
    public Instant getExpiry(String token) {
        return parse(token).getExpiration().toInstant();
    }
}
