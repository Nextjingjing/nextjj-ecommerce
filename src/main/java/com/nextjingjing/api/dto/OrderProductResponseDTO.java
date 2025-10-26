package com.nextjingjing.api.dto;

import lombok.Data;

@Data
public class OrderProductResponseDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
}
