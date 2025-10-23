package com.nextjingjing.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextjingjing.api.dto.UserRegisterRequestDto;
import com.nextjingjing.api.dto.UserResponseDto;
import com.nextjingjing.api.dto.LoginRequestDto;
import com.nextjingjing.api.dto.LoginResponseDto;
import com.nextjingjing.api.service.UserService;
import com.nextjingjing.api.util.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public UserResponseDto register(@Valid @RequestBody UserRegisterRequestDto req) {
        return userService.registerUser(req);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto res = new LoginResponseDto();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            String token = jwtUtil.generateToken(request.getUsername());

            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .sameSite("Strict")
                    .build();

            res.setStatus("success");
            res.setMessage("Login successful!");
            res.setToken(token);

            return ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())
                    .body(res);

        } catch (AuthenticationException e) {
            res.setStatus("fail");
            res.setMessage("Invalid username or password!");
            res.setToken(null);
            return ResponseEntity.status(401).body(res);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(Map.of("status", "success", "message", "Logged out successfully"));
    }

}
