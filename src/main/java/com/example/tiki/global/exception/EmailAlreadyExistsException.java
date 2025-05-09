package com.example.tiki.global.exception;

// 이미 존재하는 이메일
public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException() {
        super("이미 사용 중인 이메일입니다.");
    }

    public EmailAlreadyExistsException(String message) {
        super(message);
    }

}
