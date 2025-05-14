package com.example.tiki.init;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.dto.UserSignupDto;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.auth.service.AuthService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserInit {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

//    @PostConstruct
    @Transactional
    public void userSignup(){
        User user1 = User.builder()
                .email("alsn0527@naver.com")
                .password(passwordEncoder.encode("1234"))
                .name("유민우")
                .dateOfBirth(LocalDate.parse("1999-05-27"))
                .introduce("안녕하세요 RB 갈게요")
                .emailPush(true)
                .role(Role.ROLE_USER)
                .build();

        User user2 = User.builder()
                .email("abc123@naver.com")
                .password(passwordEncoder.encode("1234"))
                .name("전동민")
                .dateOfBirth(LocalDate.parse("1999-12-15"))
                .introduce("안녕하세요 LW 갈게요")
                .emailPush(true)
                .role(Role.ROLE_USER)
                .build();

        authRepository.save(user1);
        authRepository.save(user2);
    }
}
