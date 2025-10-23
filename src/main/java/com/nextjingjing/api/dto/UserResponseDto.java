package com.nextjingjing.api.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private String status;
    private String message;
    private String username;
    private String email;
}
