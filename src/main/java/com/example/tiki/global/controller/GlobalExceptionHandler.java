package com.example.tiki.global.controller;

import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.global.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<?> _401Exception(InvalidVerificationCodeException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler({EmailAlreadyExistsException.class, IllegalStateException.class})
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
}
