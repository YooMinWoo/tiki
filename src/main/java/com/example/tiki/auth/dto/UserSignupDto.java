package com.example.tiki.auth.dto;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserSignupDto {

    private String email;
    private String password;
    private String name;
    private LocalDate dateOfBirth;
    private String introduce;

    private boolean emailPush;

    @Schema(hidden = true)
    private Role role;

    public static User toEntity(UserSignupDto userSignupDto){
        return User.builder()
                .email(userSignupDto.getEmail())
                .password(userSignupDto.getPassword())
                .name(userSignupDto.getName())
                .dateOfBirth(userSignupDto.getDateOfBirth())
                .introduce(userSignupDto.getIntroduce())
                .emailPush(userSignupDto.isEmailPush())
                .role(userSignupDto.getRole())
                .build();
    }
}
