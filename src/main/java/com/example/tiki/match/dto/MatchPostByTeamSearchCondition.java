package com.example.tiki.match.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MatchPostByTeamSearchCondition {
    private String keyword;
    private LocalDate matchDate; // 경기 날짜
    private MatchPostByTeamStatusVisible status;       // OPEN, COMPLETED or null(전체)
    private String region;       // 지역 검색
}
