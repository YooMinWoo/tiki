package com.example.tiki.auth.domain;

import com.example.tiki.auth.dto.UserSignupDto;
import com.example.tiki.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String email;
    private String password;
    private String name;
    private LocalDate dateOfBirth;  // 생년월일
    private String introduce;       // 자기소개

    private boolean emailPush;      // 이메일 푸시알림 유무

    @Enumerated(EnumType.STRING)
    private Role role;

}
