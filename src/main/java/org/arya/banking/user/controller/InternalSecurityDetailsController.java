package org.arya.banking.user.controller;

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
public class InternalSecurityDetailsController {

    private final SecurityDetailsService securityDetailsService;

    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, String>> updateLoginAttempts(@PathVariable String userId, @RequestParam boolean loginFailed) {
        UpdateSecurityDetailsDto updateSecurityDetailsDto = new UpdateSecurityDetailsDto(null, loginFailed);
        return ResponseEntity.ok(securityDetailsService.updateSecurityCredentials(userId, updateSecurityDetailsDto));
    }
}
