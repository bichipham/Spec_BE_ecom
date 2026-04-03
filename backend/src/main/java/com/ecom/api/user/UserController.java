package com.ecom.api.user;

import com.ecom.api.user.dto.UserDtos.UserCreateRequest;
import com.ecom.api.user.dto.UserDtos.UserResponse;
import com.ecom.api.user.dto.UserDtos.UserUpdateRequest;
import com.ecom.application.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * T036 – REST controller for {@code /api/v1/users}.
 */
@Tag(name = "Users", description = "User management – CRUD")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "List all users")
    @ApiResponse(responseCode = "200", description = "User list returned")
    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @Operation(summary = "Get user by id")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable String id) {
        return userService.findById(id);
    }

    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "400", description = "Validation error or duplicate email")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserCreateRequest req) {
        return userService.create(req);
    }

    @Operation(summary = "Update user (full replacement)")
    @ApiResponse(responseCode = "200", description = "User updated")
    @ApiResponse(responseCode = "400", description = "Validation error or duplicate email")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable String id,
                               @Valid @RequestBody UserUpdateRequest req) {
        return userService.update(id, req);
    }

    @Operation(summary = "Delete user")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) {
        userService.deleteById(id);
    }
}
