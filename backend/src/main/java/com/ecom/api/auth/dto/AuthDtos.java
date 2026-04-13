package com.ecom.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * T045/T046 – DTO namespace for authentication operations (Phase 2).
 *
 * <ul>
 *   <li>{@link LoginRequest}  – POST body for {@code /api/v1/auth/login}.</li>
 *   <li>{@link LoginResponse} – Response with JWT and token metadata.</li>
 * </ul>
 */
public final class AuthDtos {

    private AuthDtos() {}

    // ── LOGIN REQUEST ─────────────────────────────────────────────────────────

    @Schema(description = "Credentials for login")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {

        @Schema(description = "Registered email address", example = "alice@example.com")
        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        private String email;

        @Schema(description = "Plain-text password", example = "s3cr3t!Pass")
        @NotBlank(message = "password is required")
        private String password;
    }

    // ── LOGIN RESPONSE ────────────────────────────────────────────────────────

    @Schema(description = "JWT access token issued after successful login")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {

        @Schema(description = "Signed JWT – pass as 'Authorization: Bearer <token>'",
                example = "eyJhbGciOiJIUzI1NiJ9...")
        private String accessToken;

        @Schema(description = "Token type – always 'Bearer'", example = "Bearer")
        private String tokenType;

        @Schema(description = "UTC expiry timestamp of the token")
        private Instant expiresAt;

        @Schema(description = "Role encoded in the token", example = "OPERATOR",
                allowableValues = {"ADMIN", "OPERATOR"})
        private String role;
    }
}
