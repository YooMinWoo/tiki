package com.example.tiki.api.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
public class KakaoAddressResponse {

    private List<Document> documents;

    @Data
    public static class Document {
        private String x;  // longitude
        private String y;  // latitude
    }
}
