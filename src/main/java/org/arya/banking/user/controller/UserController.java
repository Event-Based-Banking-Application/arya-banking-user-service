package org.arya.banking.user.controller;

import org.arya.banking.user.dto.UserDto;
import org.arya.banking.user.dto.UserResponse;
import org.arya.banking.user.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<UserResponse> registerUserEntity(@RequestBody UserDto userDto) {
        
        return ResponseEntity.ok().body(userService.register(userDto));
    }

}
