package org.arya.banking.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.arya.banking.common.dto.UserResponse;
import org.arya.banking.user.dto.UpdateSecurityDetailsDto;
import org.arya.banking.user.service.SecurityDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/api/security-details")
@RequiredArgsConstructor
@Tag(name = "Internal Security", description = "Internal service-to-service endpoints for security credential updates (login attempt tracking)")
public class InternalSecurityDetailsController {

    private final SecurityDetailsService securityDetailsService;

    @PutMapping("/{userId}")
    @Operation(summary = "Track login attempt", description = "Internally updates the login failure count for a user. Called by the auth-service after a failed authentication attempt.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login attempt recorded"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Map<String, String>> updateLoginAttempts(
            @Parameter(description = "Unique user identifier") @PathVariable String userId,
            @Parameter(description = "Whether the login attempt failed") @RequestParam boolean loginFailed) {
        UpdateSecurityDetailsDto updateSecurityDetailsDto = new UpdateSecurityDetailsDto(null, loginFailed);
        return ResponseEntity.ok(securityDetailsService.updateSecurityCredentials(userId, updateSecurityDetailsDto));
    }
}
