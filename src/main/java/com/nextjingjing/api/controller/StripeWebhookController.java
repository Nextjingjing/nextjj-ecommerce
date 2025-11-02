package com.nextjingjing.api.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nextjingjing.api.service.OrderService;
import com.nextjingjing.api.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/stripe/webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    private final Gson gson = new Gson();

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            log.error("❌ Invalid Stripe signature");
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        try {
            String type = event.getType();

            if ("payment_intent.succeeded".equals(type) || "payment_intent.payment_failed".equals(type)) {
                String rawJson = event.getDataObjectDeserializer().getRawJson();
                JsonObject json = gson.fromJson(rawJson, JsonObject.class);
                JsonObject metadata = json.has("metadata") ? json.getAsJsonObject("metadata") : null;

                if (metadata != null && metadata.has("order_id")) {
                    Long orderId = metadata.get("order_id").getAsLong();
                    if ("payment_intent.succeeded".equals(type)) {
                        paymentService.updatePaymentStatus(orderId, "SUCCEEDED");
                        orderService.updateOrderStatus(orderId, "PAID");
                        log.info("✅ Payment succeeded → Order ID: {}", orderId);
                    } else {
                        paymentService.updatePaymentStatus(orderId, "FAILED");
                        orderService.updateOrderStatus(orderId, "PAYMENT_FAILED");
                        log.warn("🚫 Payment failed → Order ID: {}", orderId);
                    }
                } else {
                    log.warn("⚠️ Missing order_id in metadata");
                }

            } else if ("charge.succeeded".equals(type)) {
                String rawJson = event.getDataObjectDeserializer().getRawJson();
                JsonObject json = gson.fromJson(rawJson, JsonObject.class);
                String paymentIntentId = json.has("payment_intent") ? json.get("payment_intent").getAsString() : null;

                if (paymentIntentId != null) {
                    try {
                        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
                        String orderIdStr = intent.getMetadata().get("order_id");
                        if (orderIdStr != null) {
                            Long orderId = Long.valueOf(orderIdStr);
                            paymentService.updatePaymentStatus(orderId, "SUCCEEDED");
                            orderService.updateOrderStatus(orderId, "PAID");
                            log.info("💵 Charge succeeded → Order ID: {}", orderId);
                        }
                    } catch (StripeException e) {
                        log.error("❌ Cannot retrieve PaymentIntent {}", paymentIntentId);
                    }
                }
            } else {
                log.debug("Unhandled Stripe event: {}", type);
            }

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            log.error("💥 Webhook processing error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error");
        }
    }
}
