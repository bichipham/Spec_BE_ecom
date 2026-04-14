package com.ecom.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * T047 – Security configuration with JWT-based RBAC (Phase 2).
 *
 * <p>Role hierarchy enforced:
 * <ul>
 *   <li><strong>ADMIN</strong> – full access to all endpoints.</li>
 *   <li><strong>OPERATOR</strong> – read + create/update for orders, order-items
 *       and products; cannot delete resources or manage users.</li>
 * </ul>
 *
 * <p>Authentication delegates to {@link JwtAuthFilter}. Sessions are stateless
 * (no HTTP session); CSRF is disabled for the REST API.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/api/v1/health",
            "/api/v1/ready",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(401);
                            res.setContentType("application/json");
                            res.getWriter().write(
                                    "{\"code\":\"UNAUTHORIZED\",\"message\":\"Authentication required\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(403);
                            res.setContentType("application/json");
                            res.getWriter().write(
                                    "{\"code\":\"FORBIDDEN\",\"message\":\"Access denied\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // ── Public ───────────────────────────────────────────
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        // Guest registration – no auth required
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        // Notifications – authenticated access required for internal/service calls
                        .requestMatchers("/api/v1/notifications/**").authenticated()
                        .requestMatchers("/api/v1/notifications").authenticated()

                        // ── Users → ADMIN only ───────────────────────────────
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users").hasRole("ADMIN")

                        // ── Products ─────────────────────────────────────────
                        // Read → ADMIN + OPERATOR
                        .requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/**")
                                .hasAnyRole("ADMIN", "OPERATOR")
                        // Write / Delete → ADMIN only
                        .requestMatchers("/api/v1/products/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/products").hasRole("ADMIN")

                        // ── Orders ───────────────────────────────────────────
                        // Delete → ADMIN only
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasRole("ADMIN")
                        // Read + Write → ADMIN + OPERATOR
                        .requestMatchers("/api/v1/orders/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/api/v1/orders").hasAnyRole("ADMIN", "OPERATOR")

                        // ── Order Items ──────────────────────────────────────
                        // Delete → ADMIN only
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/order-items/**").hasRole("ADMIN")
                        // Read + Write → ADMIN + OPERATOR
                        .requestMatchers("/api/v1/order-items/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/api/v1/order-items").hasAnyRole("ADMIN", "OPERATOR")

                        // ── Anything else requires authentication ────────────
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

