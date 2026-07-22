package org.arya.banking.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.arya.banking.common.dto.UserResponse;
import org.arya.banking.common.model.User;
import org.arya.banking.user.dto.RegisterDto;
import org.arya.banking.user.dto.UserUpdateDto;
import org.arya.banking.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints for registration, retrieval, and profile updates")
public class UserController {
    
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account. Registers the user in both the local database and Keycloak. The password must be at least 15 characters with uppercase, lowercase, digit, and special character.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed on input fields")
    })
    public ResponseEntity<UserResponse> registerUserEntity(@Valid @RequestBody RegisterDto registerDto) {
        return ResponseEntity.ok().body(userService.register(registerDto));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves the full user profile including contact numbers, addresses, and account status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "Unique user identifier") @PathVariable String userId) {
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user details", description = "Updates user profile information including contact details, address, and lock status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "Unique user identifier") @PathVariable String userId,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return ResponseEntity.ok(userService.updateUser(userId, userUpdateDto));
    }

}
