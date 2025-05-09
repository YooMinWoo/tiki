package com.example.tiki.global.exception;

// 코드값 불일치
public class InvalidVerificationCodeException extends RuntimeException{

    public InvalidVerificationCodeException() {
        super("유효하지 않은 코드입니다.");
    }

    public InvalidVerificationCodeException(String message) {
        super(message);
    }

}
