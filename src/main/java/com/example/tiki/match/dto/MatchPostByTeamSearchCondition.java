package com.example.tiki.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MatchPostByTeamSearchCondition {

    @Schema(description = "검색 키워드 (2~10자, 공백 없음)", example = "친선경기")
    @Size(min = 2, max = 10, message = "키워드는 2~10자여야 합니다.")
    private String keyword;

    @Schema(description = "경기 날짜 (오늘 이후만 가능)", example = "2025-06-10")
    @FutureOrPresent(message = "경기 날짜는 오늘 이후여야 합니다.")
    private LocalDate matchDate; // 경기 날짜

    private MatchPostByTeamStatusVisible status;       // OPEN, COMPLETED or null(전체)

    @Schema(description = "지역명 (선택)", example = "서울특별시")
    private String region;       // 지역 검색
}
