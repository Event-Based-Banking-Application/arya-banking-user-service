package org.arya.banking.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.arya.banking.user.dto.UpdateSecurityDetailsDto;
import org.arya.banking.user.service.SecurityDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/security-details")
@Tag(name = "Security Details", description = "User security credentials management — security questions and login attempt tracking")
public class SecurityDetailsController {

    private final SecurityDetailsService securityDetailsService;

    @PutMapping("/{userId}")
    @Operation(summary = "Update security credentials", description = "Updates the security questions and/or login failure status for a user. Used to reset security questions or mark login failures.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Security details updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Map<String, String>> updateSecurityDetails(
            @Parameter(description = "Unique user identifier") @PathVariable String userId,
            @Valid @RequestBody UpdateSecurityDetailsDto updateSecurityDetailsDto) {
        return ResponseEntity.ok(securityDetailsService.updateSecurityCredentials(userId, updateSecurityDetailsDto));
    }

}
