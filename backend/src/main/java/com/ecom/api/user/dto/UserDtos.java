package com.ecom.api.user.dto;

import com.ecom.domain.model.User;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserCreateRequest {

        @NotBlank(message = "name is required")
        @Size(min = 2, max = 120, message = "name must be between 2 and 120 characters")
        private String name;

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        private String email;

        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be at least 8 characters")
        private String password;

        @NotNull(message = "role is required")
        private User.Role role;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserUpdateRequest {

        @NotBlank(message = "name is required")
        @Size(min = 2, max = 120, message = "name must be between 2 and 120 characters")
        private String name;

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        private String email;

        @NotNull(message = "role is required")
        private User.Role role;
    }

    // ── RESPONSE ──────────────────────────────────────────────────────────────

    /** Password is intentionally absent from this DTO. */
    @Value
    @Builder
    public static class UserResponse {
        String id;
        String name;
        String email;
        User.Role role;
        Instant createdAt;
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
