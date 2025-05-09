package com.example.tiki.auth.security.handler;//package com.guideme.guideme.security.handler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.guideme.guideme.global.dto.ApiResponse;
//import com.guideme.guideme.global.exception.ProviderMismatchException;
//import com.guideme.guideme.global.exception.UserNotRegisteredException;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//// 로그인 실패시 작동하는 핸들러
//@Component
//public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
//
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
//                                        AuthenticationException exception) throws ServletException, IOException {
//
//        ApiResponse<?> apiResponse = null;
//        ObjectMapper objectMapper = new ObjectMapper();
//
//
//        // 예외 메시지를 추출
//        String errorMessage = "로그인에 실패했습니다.";
//        if (exception instanceof UserNotRegisteredException) {
//            // 등록된 회원이 아닌 경우 / 소셜 연동이 되어있지 않을 경우
//            // 해당사항은 분리할 필요가 있어보인다.
//            apiResponse = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), exception.getMessage(), null);
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        } else if (exception instanceof ProviderMismatchException) {
//            // 소셜 연동이 일치하지 않을 경우
//            apiResponse = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), exception.getMessage(), null);
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        } else if (exception instanceof BadCredentialsException) {
//            // 인증번호가 틀렸거나 / 인증번호를 발급받지 않았을 경우
//            apiResponse = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), exception.getMessage(), null);
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        }
//        response.setContentType("application/json;charset=UTF-8");
//        String responseBody = objectMapper.writeValueAsString(apiResponse);
//        response.getWriter().write(responseBody);
//
//    }
//}
