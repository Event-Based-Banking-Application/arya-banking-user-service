package org.arya.banking.user.controller;

import jakarta.validation.Valid;
import org.arya.banking.common.model.User;
import org.arya.banking.user.dto.RegisterDto;
import org.arya.banking.user.dto.UserResponse;
import org.arya.banking.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUserEntity(@Valid @RequestBody RegisterDto registerDto) {
        return ResponseEntity.ok().body(userService.register(registerDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }

}
