package com.nextjingjing.api.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private String status;
    private String message;
    private String token;
}
