package com.example.tiki.init;

import com.example.tiki.auth.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//@Component
//@RequiredArgsConstructor
//public class InitDB implements ApplicationRunner {
//
//    private final UserInit userInit;
//
//
//    @Override
//    @Transactional
//    public void run(ApplicationArguments args) throws Exception {
//        User user = userInit.userSignup();      // 일반 회원
//    }
//}
