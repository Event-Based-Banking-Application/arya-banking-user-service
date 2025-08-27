package org.arya.banking.user.controller;

import jakarta.validation.Valid;
import org.arya.banking.common.model.User;
import org.arya.banking.user.dto.UserDto;
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
    public ResponseEntity<UserResponse> registerUserEntity(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok().body(userService.register(userDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }

}
