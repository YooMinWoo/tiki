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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "회원 관련 기능")
public class AuthController {

    private final AuthService authService;
    private final EmailUtil emailUtil;
    private final RedisService redisService;

    @PostMapping("/signup")
    @Operation(summary = "유저 회원가입",
            description = """
                유저 회원가입 API
                
                회원가입은 다음 절차로 진행됩니다:
                
                1. 이메일 인증번호 요청: `/api/auth/email`
                2. 이메일 인증번호 확인: `/api/auth/email/verify`
                3. 사용자 or 관리자 회원가입: `/api/auth/signup`, `/signup-admin`
                
                인증이 완료되지 않으면 회원가입은 실패합니다.
                """
    )
    public ResponseEntity<?> userSignup(@RequestBody UserSignupDto userSignupDto) {
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
                """
    )
    public ResponseEntity<?> adminSignup(@RequestBody AdminSignupDto adminSignupDto) {
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
    public ResponseEntity<?> verifyEmailCode(@RequestParam("email") String email,
                                             @RequestParam("code") String code) {
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
    public ResponseEntity<?> login(@RequestParam("email") String email,
                                   @RequestParam("password") String password,
                                   HttpServletResponse response) {
        TokenDto tokenDto = authService.login(email, password);
        response.setHeader("Set-Cookie", tokenDto.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("로그인 성공!", tokenDto.getAccessToken()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    @Operation(summary = "마이페이지",
            description = "마이페이지 조회 API (로그인 했을 경우에만 접속 가능)"
    )
    public ResponseEntity<?> myPage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        MyPageDto myPageDto = MyPageDto.toDto(customUserDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("my!", myPageDto));
    }
}
