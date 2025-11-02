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

    public PaymentIntent createPaymentIntent(Double amount, String currency, Order order) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", Math.round(amount * 100));
        params.put("currency", (currency != null ? currency.toLowerCase() : defaultCurrency));
        params.put("automatic_payment_methods", Map.of("enabled", true));

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("order_id", order.getId().toString());
        params.put("metadata", metadata);

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

    @Transactional
    public void updatePaymentStatus(Long orderId, String status) {
        Payment payment = paymentRepository.findByOrder_Id(orderId);
        if (payment != null) {
            payment.setStatus(status);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);
            log.info("✅ Payment status updated to {} for order {}", status, orderId);
        } else {
            log.warn("⚠️ No payment record found for order {}", orderId);
        }
    }
}
