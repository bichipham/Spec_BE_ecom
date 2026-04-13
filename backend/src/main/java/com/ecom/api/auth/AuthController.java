package com.ecom.api.auth;

import com.ecom.api.auth.dto.AuthDtos.LoginRequest;
import com.ecom.api.auth.dto.AuthDtos.LoginResponse;
import com.ecom.application.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * T046 – Auth endpoint for login (Phase 2).
 *
 * <p>{@code POST /api/v1/auth/login} authenticates with email + password and
 * returns a signed JWT. The token must be passed as
 * {@code Authorization: Bearer <token>} on subsequent requests.
 */
@Tag(name = "Auth", description = "Authentication – login")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Login and receive a JWT access token",
            operationId = "login",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
