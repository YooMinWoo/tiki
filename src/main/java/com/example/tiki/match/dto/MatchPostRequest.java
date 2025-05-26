package com.example.tiki.match.dto;

import com.example.tiki.match.domain.enums.MatchStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class MatchPostRequest {

    private Long hostTeamId;        // 게시글 올린 팀 (주최)

    private String title;
    private String content;

    private LocalDate matchDate;  // 경기 날짜
    private LocalTime startTime;  // 경기 시작 시각 (예: 08:00)
    private LocalTime endTime;    // 경기 종료 시각 (예: 10:00)

    private String region;          // 시/도
    private String city;            // 시/군/구
    private String roadName;        // 도로명
    private String buildingNumber;  // 건물번호
    private String detailAddress;   // 상세주소
}
