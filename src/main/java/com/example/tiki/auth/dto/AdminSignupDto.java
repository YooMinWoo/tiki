package com.example.tiki.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminSignupDto extends UserSignupDto{

    @Schema(description = "비밀번호 (8~16자)", example = "admin-code", required = true)
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String adminCode;
}
