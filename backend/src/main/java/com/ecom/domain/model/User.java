package com.ecom.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * T016 – User domain entity.
 *
 * <p>Stored in {@code data/users.json}. The {@code password} field holds a
 * BCrypt hash – never plain text. Jackson's SNAKE_CASE naming strategy
 * (configured in {@code application.yml}) maps {@code createdAt} ↔ {@code created_at}.
 *
 * <p>Role constraints:
 * <ul>
 *   <li>{@link Role#ADMIN} – full CRUD + user management access.</li>
 *   <li>{@link Role#OPERATOR} – day-to-day order/product operations.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String id;
    private String name;
    private String email;
    /** BCrypt-hashed – excluded from API responses by {@link com.ecom.api.user.dto.UserDtos.UserResponse}. */
    private String password;
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;

    public enum Role {
        ADMIN,
        OPERATOR
    }
}
