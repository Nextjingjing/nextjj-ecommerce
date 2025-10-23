package com.nextjingjing.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nextjingjing.api.dto.UserRegisterRequestDto;
import com.nextjingjing.api.dto.UserResponseDto;
import com.nextjingjing.api.entity.User;
import com.nextjingjing.api.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
}
