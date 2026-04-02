package com.ecom.api.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * T010 – Readiness endpoint: {@code GET /api/v1/ready}
 *
 * <p>Returns {@code 200 OK} with {@code {"status":"READY"}} when the application
 * is ready to accept traffic (e.g., data store accessible, warm-up done).
 * Return 503 to signal not-ready in a future implementation.
 */
@Tag(name = "Health", description = "Liveness and readiness probes")
@RestController
@RequestMapping("/api/v1")
public class ReadinessController {

    @Operation(summary = "Readiness check")
    @GetMapping("/ready")
    public ResponseEntity<Map<String, String>> ready() {
        return ResponseEntity.ok(Map.of("status", "READY"));
    }
}
