package com.nextjingjing.api.dto;

import java.util.List;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDTO {

    @NotNull(message = "Order items are required")
    private List<OrderProductRequestDTO> items;
}
