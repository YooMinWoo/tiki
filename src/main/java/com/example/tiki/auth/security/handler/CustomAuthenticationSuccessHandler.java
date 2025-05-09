package com.example.tiki.auth.security.handler;//package com.guideme.guideme.security.handler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.guideme.guideme.global.dto.ApiResponse;
//import com.guideme.guideme.security.jwt.JwtUtil;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.Iterator;
//
//
//// // 로그인 성공시 작동하는 핸들러
//@Component
//@RequiredArgsConstructor
//public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    private final JwtUtil jwtUtil;
//
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        // 휴대폰 번호
//        String username = authentication.getName();
//
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//        String role = auth.getAuthority();
//
//        // 액세스토큰 (1시간)
//        String access = jwtUtil.createJwt("access", username, role, 60*60*1000L);
//
//        // 리프레쉬토큰 (24시간)
//        String refresh = jwtUtil.createJwt("refresh", username, role, 24*60*60*1000L);
//
////        System.out.println("액세스토큰: " + access);
//        //응답 설정
//
//
//        ApiResponse<?> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "로그인 성공!", null);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String responseBody = objectMapper.writeValueAsString(apiResponse);
//
//        response.setHeader("access", access);
//        response.addCookie(createCookie("refresh", refresh));
//        response.setContentType("application/json;charset=UTF-8");
//        response.setStatus(HttpStatus.OK.value());
//        response.getWriter().write(responseBody);
//    }
//
//    private Cookie createCookie(String key, String value) {
//
//        Cookie cookie = new Cookie(key, value);
//        cookie.setMaxAge(60*60*60);
//        //cookie.setSecure(true);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//
//        return cookie;
//    }
//}
//
