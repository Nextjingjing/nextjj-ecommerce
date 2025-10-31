package com.nextjingjing.api.controller;

import com.nextjingjing.api.dto.PaymentRequest;
import com.nextjingjing.api.dto.PaymentResponse;
import org.springframework.security.core.Authentication;
import com.nextjingjing.api.entity.Order;
import com.nextjingjing.api.service.CustomUserDetails;
import com.nextjingjing.api.service.OrderService;
import com.nextjingjing.api.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PaymentController {
    @Autowired
    private final PaymentService paymentService;
    @Autowired
    private final OrderService orderService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(
        @RequestBody PaymentRequest request,
        Authentication authentication
        ) {
        try {
            Order order = orderService.findById(request.getOrderId());

            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            if(!order.getUser().getId().equals(user.getUserId())) {
                throw new SecurityException("You are not authorized to pay for this order.");
            }

            PaymentIntent intent = paymentService.createPaymentIntent(
                    order.getTotalAmount(),
                    request.getCurrency(),
                    order
            );

            return ResponseEntity.ok(new PaymentResponse(intent.getClientSecret()));

        } catch (StripeException e) {
            log.error("Stripe error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (SecurityException e) {
            log.warn("Unauthorized payment attempt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}
