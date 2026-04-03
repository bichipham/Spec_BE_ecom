package com.ecom.application.user;

import com.ecom.api.common.ResourceNotFoundException;
import com.ecom.api.user.dto.UserDtos.UserCreateRequest;
import com.ecom.api.user.dto.UserDtos.UserResponse;
import com.ecom.api.user.dto.UserDtos.UserUpdateRequest;
import com.ecom.domain.model.User;
import com.ecom.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * T032 – Application service for User CRUD.
 *
 * <p>Enforces business rules:
 * <ul>
 *   <li>E-mail must be unique across all users.</li>
 *   <li>Password is BCrypt-hashed before persistence (plain text never stored).</li>
 * </ul>
 *
 * <p>Phase 2 note: {@link BCryptPasswordEncoder} is instantiated here directly.
 * When {@code PasswordConfig} bean is added in T044, replace this field with
 * an injected {@code PasswordEncoder} dependency.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Phase-1 local instance; will be replaced by injected bean in Phase 2 (T044)
    @SuppressWarnings("java:S2259")
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserResponse create(UserCreateRequest req) {
        userRepository.findByEmail(req.getEmail()).ifPresent(existing -> {
            throw new IllegalArgumentException("Email already registered: " + req.getEmail());
        });

        Instant now = Instant.now();
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .createdAt(now)
                .updatedAt(now)
                .build();

        log.info("Creating user email={}", req.getEmail());
        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse findById(String id) {
        return UserResponse.from(
                userRepository.findById(id)
                        .orElseThrow(() -> ResourceNotFoundException.of("User", id))
        );
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public UserResponse update(String id, UserUpdateRequest req) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));

        // E-mail uniqueness check only when the address actually changes
        if (!existing.getEmail().equalsIgnoreCase(req.getEmail())) {
            userRepository.findByEmail(req.getEmail()).ifPresent(conflict -> {
                throw new IllegalArgumentException("Email already registered: " + req.getEmail());
            });
        }

        existing.setName(req.getName());
        existing.setEmail(req.getEmail());
        existing.setRole(req.getRole());
        existing.setUpdatedAt(Instant.now());

        log.info("Updating user id={}", id);
        return UserResponse.from(userRepository.save(existing));
    }

    public void deleteById(String id) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException.of("User", id);
        }
        log.info("Deleting user id={}", id);
        userRepository.deleteById(id);
    }
}
