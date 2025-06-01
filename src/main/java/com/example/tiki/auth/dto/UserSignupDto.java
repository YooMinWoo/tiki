package com.example.tiki.auth.dto;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupDto {

    @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "비밀번호 (8~16자)", example = "securePass123", required = true)
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 8~16자로 입력해야 합니다.")
    private String password;

    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Schema(description = "생년월일 (yyyy-MM-dd)", example = "1990-01-01", required = true)
    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate dateOfBirth;

    @Schema(description = "자기소개 (20~300자)", example = "안녕하세요, 저는 축구를 좋아하고 팀 활동을 즐깁니다.")
    @Size(min = 20, max = 300, message = "자기소개는 20자 이상, 300자 이하여야 합니다.")
    private String introduce;

    @Schema(description = "이메일 알림 수신 여부", example = "true")
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
