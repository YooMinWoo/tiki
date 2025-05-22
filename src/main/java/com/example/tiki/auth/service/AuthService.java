package com.example.tiki.auth.service;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.dto.UserSignupDto;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.auth.security.jwt.JwtUtil;
import com.example.tiki.auth.security.jwt.TokenDto;
import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.global.exception.*;
import com.example.tiki.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final static String adminCode = "admin-code";
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void checkEmailDuplicate(String email){
        if(authRepository.findByEmail(email).isPresent()) throw new EmailAlreadyExistsException();
    }

    public User signup(UserSignupDto signupDto, Role role, String code) {
        // 1. 이메일 중복 확인
        checkEmailDuplicate(signupDto.getEmail());

        // 2. 이메일 인증 확인
        String isVerified = redisService.get("email:verified:" + signupDto.getEmail());
        if (!"true".equals(isVerified)) throw new EmailNotVerifiedException();

        // 3. 관리자 코드 검증
        if (role == Role.ROLE_ADMIN && !adminCode.equals(code)) throw new InvalidVerificationCodeException("관리자 코드가 일치하지 않습니다.");

        // 4. 권한 부여
        signupDto.setRole(role);

        // 5. 비밀번호 암호화
        signupDto.setPassword(passwordEncoder.encode(signupDto.getPassword()));

        User user = UserSignupDto.toEntity(signupDto);
        return authRepository.save(user);
    }

    public TokenDto login(String email, String password) {
        User user = authRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("존재하지 않는 계정입니다."));
        if(!passwordEncoder.matches(password, user.getPassword())) throw new InvalidVerificationCodeException("비밀번호가 일치하지 않습니다.");

        return jwtUtil.generateToken(user);
    }

    public TokenDto refreshToken(String refreshToken) {
        try{
            jwtUtil.isExpired(refreshToken);
            Authentication authentication = jwtUtil.getAuthentication(refreshToken);
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = customUserDetails.getUser();

            return jwtUtil.generateToken(user);
        } catch (Exception e){
            throw new CustomException("로그인 창으로 이동");
        }
    }
}
