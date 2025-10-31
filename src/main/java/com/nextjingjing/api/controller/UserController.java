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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nextjingjing.api.dto.OrderResponseDTO;
import com.nextjingjing.api.dto.UserInfoResponseDto;
import com.nextjingjing.api.dto.UserUpdateRequestDto;
import com.nextjingjing.api.service.CustomUserDetails;
import com.nextjingjing.api.service.OrderService;
import com.nextjingjing.api.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/orders")
    public ResponseEntity<?> getUserOrders(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var orders = orderService.getMyOrders(id, page, size);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable Long userId,
            @PathVariable Long orderId,
            Authentication authentication) {
        OrderResponseDTO order = orderService.getOrderById(userId, orderId);
        return ResponseEntity.ok(order);
    }
}
