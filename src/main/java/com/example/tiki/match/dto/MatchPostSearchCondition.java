package com.example.tiki.match.dto;

import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class MatchPostSearchCondition {
    private String keyword;
    private LocalDate matchDate; // 경기 날짜
    private MatchPostStatusVisible status;       // OPEN, COMPLETED or null(전체)
    private String region;       // 지역 검색
}
