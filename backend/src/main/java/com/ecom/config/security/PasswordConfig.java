package com.ecom.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * T044 – Centralized password encoder configuration (Phase 2).
 *
 * <p>Exposes a single {@link PasswordEncoder} bean backed by BCrypt so that
 * every service (e.g. {@code UserService}, {@code AuthService}) shares the same
 * encoder without duplicating instantiation.
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
