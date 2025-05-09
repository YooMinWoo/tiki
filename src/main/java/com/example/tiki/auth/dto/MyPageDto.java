package com.example.tiki.auth.dto;

import com.example.tiki.auth.domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MyPageDto {

    private String email;
    private String name;
    private LocalDate dateOfBirth;  // 생년월일
    private String introduce;       // 자기소개

    private boolean emailPush;      // 이메일 푸시알림 유무

    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;

    public static MyPageDto toDto(User user){
        return MyPageDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .dateOfBirth(user.getDateOfBirth())
                .introduce(user.getIntroduce())
                .emailPush(user.isEmailPush())
                .createDate(user.getCreatedDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .build();
    }
}
