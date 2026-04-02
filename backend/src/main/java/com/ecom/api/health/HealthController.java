package com.ecom.api.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * T009 – Liveness health endpoint: {@code GET /api/v1/health}
 *
 * <p>Returns {@code 200 OK} with {@code {"status":"UP"}} as long as the application
 * process is alive and the Spring context is healthy.
 */
@Tag(name = "Health", description = "Liveness and readiness probes")
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @Operation(summary = "Liveness check")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
