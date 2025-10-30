package com.nextjingjing.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextjingjing.api.dto.UserRegisterRequestDto;
import com.nextjingjing.api.dto.UserResponseDto;
import com.nextjingjing.api.dto.LoginRequestDto;
import com.nextjingjing.api.dto.LoginResponseDto;
import com.nextjingjing.api.service.CustomUserDetails;
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

    @GetMapping("/test")
    public String privateCheck() {
        return "Hello Protect";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test/admin")
    public String getMethodName() {
        return "You are admin";
    }

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", customUser.getUserId());
        userInfo.put("username", customUser.getUsername());
        userInfo.put("roles", customUser.getAuthorities());

        return ResponseEntity.ok(userInfo);
    }

}
