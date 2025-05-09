package com.example.tiki.global.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

    @GetMapping("/default")
    public String defaultPage(){
        return "기본 페이지입니다.";
    }

    @GetMapping("/")
    public String mainPage(){ return "guideme 메인페이지."; }
}
