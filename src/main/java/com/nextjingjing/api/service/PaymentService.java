package com.nextjingjing.api.service;

import com.nextjingjing.api.entity.Order;
import com.nextjingjing.api.entity.Payment;
import com.nextjingjing.api.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.secret.key}")
    private String secretKey;

    private final String defaultCurrency = "thb";

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
        log.info("Stripe API Key initialized successfully");
    }

    public PaymentIntent createPaymentIntent(Double amount, String currency, Order order) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", Math.round(amount * 100)); // Stripe uses smallest currency unit
        params.put("currency", (currency != null ? currency.toLowerCase() : defaultCurrency));
        params.put("automatic_payment_methods", Map.of("enabled", true));

        PaymentIntent intent = PaymentIntent.create(params);

        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setMethod("stripe");
        payment.setStatus(intent.getStatus());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOrder(order);
        paymentRepository.save(payment);

        return intent;
    }

    @Transactional
    public void recordPayment(Order order, PaymentIntent intent) {
        Payment payment = new Payment();
        payment.setAmount(intent.getAmount() / 100.0);
        payment.setMethod("stripe");
        payment.setStatus(intent.getStatus());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOrder(order);
        paymentRepository.save(payment);
    }
}
