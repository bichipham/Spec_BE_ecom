package com.ecom.application.auth;

import com.ecom.api.auth.dto.AuthDtos.LoginRequest;
import com.ecom.api.auth.dto.AuthDtos.LoginResponse;
import com.ecom.config.security.JwtTokenProvider;
import com.ecom.domain.model.User;
import com.ecom.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * T045 – Authentication service for login (Phase 2).
 *
 * <p>Validates credentials, issues a signed JWT, and returns token metadata.
 * A generic "Invalid credentials" message is always used on failure to avoid
 * leaking which of e-mail or password was wrong (prevents user enumeration).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticate a user and return a JWT access token.
     *
     * @throws BadCredentialsException if e-mail is not found or password does not match
     */
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = tokenProvider.generate(user.getId(), user.getRole());
        Instant expiresAt = tokenProvider.getExpiry(token);

        log.info("Successful login: userId={} role={}", user.getId(), user.getRole());
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresAt(expiresAt)
                .role(user.getRole().name())
                .build();
    }
}
