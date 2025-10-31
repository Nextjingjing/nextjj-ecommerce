package com.nextjingjing.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextjingjing.api.dto.UserInfoResponseDto;
import com.nextjingjing.api.dto.UserUpdateRequestDto;
import com.nextjingjing.api.service.CustomUserDetails;
import com.nextjingjing.api.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateUserInfo(
        @RequestBody UserUpdateRequestDto dto,
        Authentication authentication
    ) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        return userService.updateUserInfo(user.getUserId(), dto);
    }
    
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/info")
    public UserInfoResponseDto getUserInfo(
        Authentication authentication
    ) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        return userService.getUserInfo(user.getUserId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("info/{userId}")
    public UserInfoResponseDto getUserInfoByAdmin(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }

}
