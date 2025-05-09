package com.example.tiki.auth.security.handler;

import com.example.tiki.global.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


// 권한이 부족한 경우 (e.g. ROLE_USER가 ROLE_ADMIN 권한 필요한 url 접근시)
@Component("AccessDeniedHandler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {


	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

		ApiResponse<?> apiResponse = new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다.", null);

		ObjectMapper objectMapper = new ObjectMapper();
		String responseBody = objectMapper.writeValueAsString(apiResponse);
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.getWriter().write(responseBody);

	}
}
