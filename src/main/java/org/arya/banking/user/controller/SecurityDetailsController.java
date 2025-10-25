package org.arya.banking.user.controller;

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
public class SecurityDetailsController {

    private final SecurityDetailsService securityDetailsService;

    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, String>> updateSecurityDetails(@PathVariable String userId, @Valid @RequestBody UpdateSecurityDetailsDto updateSecurityDetailsDto) {
        return ResponseEntity.ok(securityDetailsService.updateSecurityCredentials(userId, updateSecurityDetailsDto));
    }

}
