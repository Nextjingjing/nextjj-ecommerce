package com.nextjingjing.api.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDTO {

    @NotNull(message = "Order items are required")
    @Valid
    private List<OrderProductRequestDTO> items;
}
