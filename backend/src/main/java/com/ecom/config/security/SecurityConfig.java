package com.ecom.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * T015 – Security skeleton.
 *
 * <p><strong>Phase 1 (MVP)</strong>: All endpoints are open so that CRUD can be
 * developed and tested without authentication. JWT-based RBAC will be enabled in
 * <strong>Phase 2</strong> (T047) once the auth layer is implemented.
 *
 * <p>CSRF is disabled because the API is stateless (no session cookies).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/api/v1/health",
            "/api/v1/ready",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        // Phase 1: open – lock down in Phase 2 (T047)
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
