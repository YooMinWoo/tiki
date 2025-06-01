package com.example.tiki.auth.controller;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.dto.AdminSignupDto;
import com.example.tiki.auth.dto.MyPageDto;
import com.example.tiki.auth.dto.UserSignupDto;
import com.example.tiki.auth.security.jwt.TokenDto;
import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.auth.service.AuthService;
import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.global.exception.InvalidVerificationCodeException;
import com.example.tiki.global.redis.RedisService;
import com.example.tiki.utils.EmailUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "회원 관련 기능")
public class AuthController {

    private final AuthService authService;
    private final EmailUtil emailUtil;
    private final RedisService redisService;

    @PostMapping("/email/check")
    @Operation(summary = "이메일 중복확인",
            description = "이메일 중복확인 API"
    )
    public ResponseEntity<?> checkEmailDuplicate(@NotBlank @Email @RequestParam("email") String email) {
        authService.checkEmailDuplicate(email);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("사용 가능한 이메일입니다!", null));
    }


    @PostMapping("/signup")
    @Operation(summary = "유저 회원가입",
            description = """
                유저 회원가입 API
                
                회원가입은 다음 절차로 진행됩니다:
                
                1. 이메일 인증번호 요청: `/api/auth/email`
                2. 이메일 인증번호 확인: `/api/auth/email/verify`
                3. 사용자 or 관리자 회원가입: `/api/auth/signup`, `/signup-admin`
                
                인증이 완료되지 않으면 회원가입은 실패합니다.
                
                📌 입력 조건:<br>
                    - 이메일: 필수, 이메일 형식<br>
                    - 비밀번호: 필수, 8~16자<br>
                    - 이름: 필수<br>
                    - 생년월일: 필수, yyyy-MM-dd, 과거 날짜<br>
                    - 자기소개: 선택, 20~300자<br>
                    - 이메일 푸시: 선택, true 또는 false
                """
    )
    public ResponseEntity<?> userSignup(@Valid @RequestBody UserSignupDto userSignupDto) {
        authService.signup(userSignupDto, Role.ROLE_USER, null);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("회원가입 성공!", null));
    }

    @PostMapping("/signup-admin")
    @Operation(summary = "관리자 회원가입",
            description = """
                관리자 회원가입 API
                
                회원가입은 다음 절차로 진행됩니다:
                
                1. 이메일 인증번호 요청: `/api/auth/email`
                2. 이메일 인증번호 확인: `/api/auth/email/verify`
                3. 사용자 or 관리자 회원가입: `/api/auth/signup`, `/signup-admin`
                4. 관리자 인증코드(adminCode 입력란) : admin-code
                
                인증이 완료되지 않으면 회원가입은 실패합니다.
                
                📌 입력 조건:<br>
                    - 이메일: 필수, 이메일 형식<br>
                    - 비밀번호: 필수, 8~16자<br>
                    - 이름: 필수<br>
                    - 생년월일: 필수, yyyy-MM-dd, 과거 날짜<br>
                    - 자기소개: 선택, 20~300자<br>
                    - 이메일 푸시: 선택, true 또는 false
                """
    )
    public ResponseEntity<?> adminSignup(@Valid @RequestBody AdminSignupDto adminSignupDto) {
        authService.signup(adminSignupDto, Role.ROLE_ADMIN, adminSignupDto.getAdminCode());
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("회원가입 성공!", null));
    }

    @PostMapping("/email")
    @Operation(summary = "이메일 인증번호 발송", description = "이메일 인증번호 발송 API")
    public ResponseEntity<?> sendEmailCode(@RequestParam("email") String email) {
        emailUtil.sendEmail(email);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("이메일 전송 선공!", null));
    }

    @PostMapping("/email/verify")
    @Operation(summary = "인증번호 체크", description = "인증번호 일치 여부 API")
    public ResponseEntity<?> verifyEmailCode(@NotBlank @Email @RequestParam("email") String email,
                                             @NotBlank @RequestParam("code") String code) {
        emailUtil.verifyCode(email,code);
        return ResponseEntity.ok(ApiResponse.success("인증 성공", null));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인",
            description = """
                    로그인 API
                    
                    주어진 access 토큰 값을 Authorize 버튼을 통해 입력하여주세요.
                    """
    )
    public ResponseEntity<?> login(@NotBlank @Email @RequestParam("email") String email,
                                   @NotBlank @RequestParam("password") String password,
                                   HttpServletResponse response) {
        TokenDto tokenDto = authService.login(email, password);
//        response.setHeader("Set-Cookie", tokenDto.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("로그인 성공!", tokenDto));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "refresh",
            description = "refresh 토큰 값을 통해 access 토큰과 refresh 토큰을 재발급 받는 API입니다. (단, refresh 토큰 값이 일치할 경우)"
    )
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) throw new RuntimeException("No cookies found");

        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("No refresh token found"));

        TokenDto tokenDto = authService.refreshToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("로그인 성공!", tokenDto));
    }

}
