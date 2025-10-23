package com.nextjingjing.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextjingjing.api.dto.UserRegisterRequestDto;
import com.nextjingjing.api.dto.UserResponseDto;
import com.nextjingjing.api.dto.LoginRequestDto;
import com.nextjingjing.api.dto.LoginResponseDto;
import com.nextjingjing.api.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserResponseDto register(@Valid @RequestBody UserRegisterRequestDto req) {
        return userService.registerUser(req);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto req) {
        return userService.login(req);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return userService.logout();
    }

}
