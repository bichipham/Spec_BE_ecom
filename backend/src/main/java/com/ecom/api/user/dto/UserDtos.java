package com.ecom.api.user.dto;

import com.ecom.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.Instant;

/**
 * T020 – DTO namespace for User CRUD operations.
 *
 * <p>Three inner classes cover the full lifecycle:
 * <ul>
 *   <li>{@link UserCreateRequest} – POST body for creating a new user.</li>
 *   <li>{@link UserUpdateRequest} – PUT body for replacing user attributes.</li>
 *   <li>{@link UserResponse}       – API response (password is omitted).</li>
 * </ul>
 */
public final class UserDtos {

    private UserDtos() {}

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Schema(description = "Request body for creating a new user")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserCreateRequest {

        @Schema(description = "Display name", example = "Alice Smith", minLength = 2, maxLength = 120)
        @NotBlank(message = "name is required")
        @Size(min = 2, max = 120, message = "name must be between 2 and 120 characters")
        private String name;

        @Schema(description = "Unique email address", example = "alice@example.com")
        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        private String email;

        @Schema(description = "Plain-text password – stored as bcrypt hash", example = "s3cr3t!Pass", minLength = 8)
        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be at least 8 characters")
        private String password;

        @Schema(description = "User role", example = "OPERATOR", allowableValues = {"ADMIN", "OPERATOR"})
        @NotNull(message = "role is required")
        private User.Role role;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Schema(description = "Request body for updating an existing user")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserUpdateRequest {

        @Schema(description = "Display name", example = "Alice Smith", minLength = 2, maxLength = 120)
        @NotBlank(message = "name is required")
        @Size(min = 2, max = 120, message = "name must be between 2 and 120 characters")
        private String name;

        @Schema(description = "Unique email address", example = "alice@example.com")
        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        private String email;

        @Schema(description = "User role", example = "OPERATOR", allowableValues = {"ADMIN", "OPERATOR"})
        @NotNull(message = "role is required")
        private User.Role role;
    }

    // ── RESPONSE ──────────────────────────────────────────────────────────────

    /** Password is intentionally absent from this DTO. */
    @Schema(description = "User resource – password is omitted")
    @Value
    @Builder
    public static class UserResponse {

        @Schema(description = "UUID identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        String id;

        @Schema(description = "Display name", example = "Alice Smith")
        String name;

        @Schema(description = "Email address", example = "alice@example.com")
        String email;

        @Schema(description = "Assigned role", example = "OPERATOR")
        User.Role role;

        @Schema(description = "Creation timestamp (UTC / ISO-8601)", example = "2024-01-15T08:30:00Z")
        Instant createdAt;

        @Schema(description = "Last-updated timestamp (UTC / ISO-8601)", example = "2024-01-15T09:00:00Z")
        Instant updatedAt;

        public static UserResponse from(User user) {
            return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }
}
