package com.nextjingjing.api.service;

import com.nextjingjing.api.entity.Order;
import com.nextjingjing.api.entity.Payment;
import com.nextjingjing.api.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.secret.key}")
    private String secretKey;

    private final String defaultCurrency = "thb";

    public PaymentIntent createPaymentIntent(Double amount, String currency, Order order) throws StripeException {
        Stripe.apiKey = secretKey;

        Optional<Payment> existingPaymentOpt = paymentRepository.findByOrder_Id(order.getId());
        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();

            if (existingPayment.getStripePaymentIntentId() != null) {
                return PaymentIntent.retrieve(existingPayment.getStripePaymentIntentId());
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("amount", Math.round(amount * 100)); // หน่วยเป็น satang
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
        payment.setClientSecret(intent.getClientSecret());
        payment.setStripePaymentIntentId(intent.getId());
        paymentRepository.save(payment);

        log.info("Created new PaymentIntent {} for order {}", intent.getId(), order.getId());
        return intent;
    }

    @Transactional
    public void recordPayment(Order order, PaymentIntent intent) {
        Stripe.apiKey = secretKey;

        Payment payment = new Payment();
        payment.setAmount(intent.getAmount() / 100.0);
        payment.setMethod("stripe");
        payment.setStatus(intent.getStatus());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOrder(order);
        payment.setClientSecret(intent.getClientSecret());
        payment.setStripePaymentIntentId(intent.getId());

        paymentRepository.save(payment);
        log.info("Recorded payment for order {} with intent {}", order.getId(), intent.getId());
    }

    @Transactional
    public void updatePaymentStatus(Long orderId, String status) {
        Optional<Payment> paymentOpt = paymentRepository.findByOrder_Id(orderId);

        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(status);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);
            log.info("Payment status updated to {} for order {}", status, orderId);
        } else {
            log.warn("No payment record found for order {}", orderId);
        }
    }
}
