package com.example.tiki.api.kakao;

import lombok.Data;

@Data
public class GeoCoordinate {

    private Double latitude;        // 위도
    private Double longitude;       // 경도

    public GeoCoordinate(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
