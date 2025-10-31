package com.nextjingjing.api.dto;

import lombok.Data;

@Data
public class UserInfoResponseDto {
    private Long userId;
    private String address;
    private String tel;
    private String fname;
    private String lname;
}
