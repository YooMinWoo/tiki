package com.example.tiki.api.kakao;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KakaoMapService {

    @Value("${kakao.api-key}")
    private String kakaoApiKey;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization", "KakaoAK " + kakaoApiKey)
                .build();
    }

    public Mono<GeoCoordinate> getCoordinates(String fullAddress) {
        return webClient.get()
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
                    KakaoAddressResponse.Document doc = response.getDocuments().get(0);
                    return new GeoCoordinate(Double.parseDouble(doc.getY()), Double.parseDouble(doc.getX()));
                });
    }
}
