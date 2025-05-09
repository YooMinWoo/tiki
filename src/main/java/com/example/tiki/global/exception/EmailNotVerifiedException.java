package com.example.tiki.global.exception;

// 이메일 인증 완료 X
public class EmailNotVerifiedException extends RuntimeException{

    public EmailNotVerifiedException() {
        super("이메일 인증이 완료되지 않았습니다.");
    }

    public EmailNotVerifiedException(String message) {
        super(message);
    }

}
