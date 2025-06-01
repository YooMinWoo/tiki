package com.example.tiki.global.controller;

import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.global.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.AlreadyBuiltException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.channels.AlreadyBoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<?> _401Exception(InvalidVerificationCodeException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler({EmailAlreadyExistsException.class, IllegalStateException.class, AlreadyBuiltException.class})
    public ResponseEntity<?> _409Exception(RuntimeException e){
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<?> _400Exception(EmailNotVerifiedException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> _403Exception(ForbiddenException e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> _404Exception(NotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> ExceptionHandler(Exception e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        // 첫 번째 오류 메시지만 추출 (원한다면 전체 목록으로도 가능)
        String errorMessage = bindingResult.getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("잘못된 요청입니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(errorMessage));
    }
}
