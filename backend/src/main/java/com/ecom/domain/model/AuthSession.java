package com.ecom.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * T042 – AuthSession domain model (Phase 2).
 *
 * <p>Represents an active login session. The raw JWT is never stored;
 * only a SHA-256 {@code tokenHash} is kept for revocation lookups.
 *
 * <p>Stored in {@code data/auth_sessions.json} (in-memory or JSON adapter in Phase 2).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthSession {

    private String sessionId;
    private String userId;
    private User.Role role;
    /** SHA-256 hash of the raw JWT – never store the token itself. */
    private String tokenHash;
    private Instant expiresAt;
    private Instant createdAt;
}
