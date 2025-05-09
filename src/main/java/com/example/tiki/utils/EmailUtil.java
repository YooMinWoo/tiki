package com.example.tiki.utils;

import com.example.tiki.global.exception.InvalidVerificationCodeException;
import com.example.tiki.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailUtil {

    private final RedisService redisService;
    private final JavaMailSender mailSender;

    public void verifyCode(String email, String code){
        String storedCode = redisService.get("email:" + email);
        if (storedCode == null || !storedCode.equals(code)) throw new InvalidVerificationCodeException("인증번호가 일치하지 않습니다.");

        // 인증 완료 상태 저장
        redisService.save("email:verified:" + email, "true", 30, TimeUnit.MINUTES);
    }

    public void sendEmail(String email){
        // 인증번호 생성
        String code = generateRandomCode();

        // 인증번호 Redis에 저장 (5분 유효)
        redisService.save("email:" + email, code, 5, TimeUnit.MINUTES);

        // 이메일 전송
        send(email, "[TIKI] 회원가입 인증번호", "인증번호: " + code);
    }

    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public String generateRandomCode() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // 6자리 숫자
        return String.valueOf(number);
    }
}
