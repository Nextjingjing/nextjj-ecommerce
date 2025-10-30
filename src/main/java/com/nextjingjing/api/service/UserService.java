package com.nextjingjing.api.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nextjingjing.api.dto.LoginRequestDto;
import com.nextjingjing.api.dto.LoginResponseDto;
import com.nextjingjing.api.dto.UserRegisterRequestDto;
import com.nextjingjing.api.dto.UserResponseDto;
import com.nextjingjing.api.dto.UserUpdateRequestDto;
import com.nextjingjing.api.entity.User;
import com.nextjingjing.api.repository.UserRepository;
import com.nextjingjing.api.util.JwtUtil;

@Service
public class UserService {
    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public UserResponseDto registerUser(UserRegisterRequestDto req) {
        UserResponseDto response = new UserResponseDto();
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            
            response.setStatus("fail");
            response.setMessage("Username already exists!");
            return response;
        }

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            response.setStatus("fail");
            response.setMessage("Email already exists!");
            return response;
        }
        User user = new User();
        user.setEmail(req.getEmail());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        
        userRepository.save(user);

        response.setStatus("success");
        response.setMessage("User registered successfully!");
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }

    public ResponseEntity<LoginResponseDto> login(LoginRequestDto request) {
        LoginResponseDto res = new LoginResponseDto();

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(userDetails.getUsername());

            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .sameSite("Strict")
                    .build();

            res.setStatus("success");
            res.setMessage("Login successful!");

            return ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())
                    .body(res);

        } catch (Exception e) {
            res.setStatus("fail");
            res.setMessage("Invalid username or password!");
            return ResponseEntity.status(401).body(res);
        }
    }

    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(Map.of("status", "success", "message", "Logged out successfully"));
    }

    public ResponseEntity<?> updateUserInfo(Long userId, UserUpdateRequestDto dto) {
        User existUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (dto.getAddress() != null && !dto.getAddress().isEmpty()) {
            existUser.setAddress(dto.getAddress());
        }
        if (dto.getTel() != null && !dto.getTel().isEmpty()) {
            existUser.setTel(dto.getTel());
        }
        if (dto.getFname() != null && !dto.getFname().isEmpty()) {
            existUser.setFname(dto.getFname());
        }
        if (dto.getLname() != null && !dto.getLname().isEmpty()) {
            existUser.setLname(dto.getLname());
        }

        userRepository.save(existUser);

        return ResponseEntity.ok(dto);
    }

}
