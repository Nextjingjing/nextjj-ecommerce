package com.nextjingjing.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    private String currency = "thb";
    @NotNull
    private Long orderId;
}
