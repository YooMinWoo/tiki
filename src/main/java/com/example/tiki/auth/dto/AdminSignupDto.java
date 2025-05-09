package com.example.tiki.auth.dto;

import lombok.Data;

@Data
public class AdminSignupDto extends UserSignupDto{

    private String adminCode;
}
