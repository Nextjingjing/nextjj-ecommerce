package com.nextjingjing.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String clientSecret;
}
