package com.nextjingjing.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderProductRequestDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    private Integer quantity;
}
