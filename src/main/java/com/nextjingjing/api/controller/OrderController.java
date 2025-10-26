package com.nextjingjing.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.nextjingjing.api.dto.OrderRequestDTO;
import com.nextjingjing.api.dto.OrderResponseDTO;
import com.nextjingjing.api.entity.User;
import com.nextjingjing.api.repository.UserRepository;
import com.nextjingjing.api.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/order")
public class OrderController {  

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @RequestBody @Valid OrderRequestDTO dto,
            Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        OrderResponseDTO created = orderService.createOrder(user.getId(), dto);

        return ResponseEntity.ok(created);
    }

}
