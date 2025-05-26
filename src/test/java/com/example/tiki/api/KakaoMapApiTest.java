package com.example.tiki.api;


import com.example.tiki.api.kakao.KakaoAddressResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class KakaoMapApiTest {

    @Test
    void just(){
        String fullAddress = "인천광역시 미추홀구 용오로82";
        String kakaoApiKey = "0ee470e3b1cb23772e9472e56cedb5cc";

        // webClient 기본 설정
        WebClient webClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization", "KakaoAK " + kakaoApiKey)
                .build();

        // api 요청
        KakaoAddressResponse result = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/address.json")
                        .queryParam("query", fullAddress)
                        .build())
                .retrieve()
                .bodyToMono(KakaoAddressResponse.class)
                .map(response -> {
                    if (response.getDocuments().isEmpty()) {
                        throw new IllegalArgumentException("주소 검색 결과가 없습니다.");
                    }
                    return response;
                })
                .block();

        System.out.println(result.getDocuments().get(0).getX());
        System.out.println(result.getDocuments().get(0).getY());

    }
}
