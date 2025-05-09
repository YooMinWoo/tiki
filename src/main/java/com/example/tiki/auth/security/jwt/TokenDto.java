package com.example.tiki.auth.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TokenDto {
    private final String accessToken;
    private final String refreshToken;
}
