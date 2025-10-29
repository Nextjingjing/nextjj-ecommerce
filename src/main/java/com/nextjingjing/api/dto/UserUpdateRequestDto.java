package com.nextjingjing.api.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateRequestDto {
    private String address;
    @Pattern(regexp = "^(0[689]{1}[0-9]{8})$", message = "Invalid Thai phone number format")
    private String tel;
    private String fname;
    private String lname;
}
