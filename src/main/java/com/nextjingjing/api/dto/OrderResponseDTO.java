package com.nextjingjing.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderResponseDTO {
    private Long id;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private Long userId;
    private List<OrderProductResponseDTO> items;
}
