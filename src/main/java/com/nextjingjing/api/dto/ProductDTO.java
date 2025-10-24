package com.nextjingjing.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    private Double price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must not be negative")
    private Integer stock;

    private Long categoryId;
}
