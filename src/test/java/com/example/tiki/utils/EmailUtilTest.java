package com.example.tiki.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EmailUtilTest {

    @Autowired EmailUtil emailUtil;

    @Test
    public void mailSend(){
        String email = "alsn0527@naver.com";

        // 인증번호 생성
        String code = emailUtil.generateRandomCode();

        // 이메일 전송
        emailUtil.send(email, "[TIKI] 회원가입 인증번호", "인증번호: " + code);

        System.out.println("발송된 인증번호: " + code);
    }
}