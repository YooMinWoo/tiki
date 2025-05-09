package com.example.tiki.auth.security.jwt;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.security.user.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {


    private SecretKey secretKey;
    private CustomUserDetailsService customUserDetailsService;

    public JwtUtil(@Value("${spring.jwt.secret}")String secret, CustomUserDetailsService customUserDetailsService) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.customUserDetailsService = customUserDetailsService;
    }

    public String getEmail(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public Authentication getAuthentication(String token) throws Exception {
        UserDetails user = customUserDetailsService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public TokenDto generateToken(User user) {
        long now = System.currentTimeMillis();
        long accessTokenExpiration = 1000 * 60 * 30; // 30분
        long refreshTokenExpiration = 1000 * 60 * 60 * 24; // 24시간


        // 액세스 토큰 생성
        String accessToken = Jwts.builder()
                .claim("category", "access")
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessTokenExpiration))
                .signWith(secretKey)
                .compact();

        // 리프레시 토큰 생성
        String refreshToken = Jwts.builder()
                .claim("category", "refresh")
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(new Date(now))
                .expiration(new Date(now + refreshTokenExpiration))
                .signWith(secretKey)
                .compact();

        // ✅ 리프레시 토큰을 HttpOnly 쿠키로 설정
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // HTTPS 환경에서만 사용할 경우, 로컬에서는 http이므로 false, 실서버에서는 https이므로 true
                .path("/")
                .maxAge(refreshTokenExpiration / 1000)
//                .sameSite("None") // Lax, Strict, None
                .build();

        // 액세스 토큰만 클라이언트에 반환
        return new TokenDto(accessToken, refreshToken);
    }

}
