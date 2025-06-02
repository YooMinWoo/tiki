package com.example.tiki.match.dto;

import com.example.tiki.match.domain.enums.MatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class MatchPostRequest {

    @NotNull(message = "hostTeamId는 필수입니다.")
    @Schema(description = "게시글 올린 팀 ID (주최)", example = "123", required = true)
    private Long hostTeamId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 5, max = 20, message = "제목은 5~20자 입력 가능합니다.")
    @Schema(description = "게시글 제목", example = "주말 축구 경기 참가자 모집", required = true)
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(min = 20, max = 1000, message = "내용은 20~1000자 입력 가능합니다.")
    @Schema(description = "게시글 내용", example = "주말 오후 2시에 축구장 앞에서 만나요!", required = true)
    private String content;

    @NotNull(message = "경기 시작 시간은 필수입니다.")
    @Future
    @Schema(description = "경기 시작 시각 , 예: 2025-06-01T08:00:00", example = "2025-06-01T08:00:00", required = true)
    private LocalDateTime startTime;

    @NotNull(message = "경기 종료 시간은 필수입니다.")
    @Future
    @Schema(description = "경기 종료 시각 , 예: 2025-06-01T10:00:00", example = "2025-06-01T10:00:00", required = true)
    private LocalDateTime endTime;

    @NotBlank(message = "시/도(region)는 필수입니다.")
    @Schema(description = "시/도", example = "서울특별시", required = true)
    private String region;

    @NotBlank(message = "시/군/구(city)는 필수입니다.")
    @Schema(description = "시/군/구", example = "강남구", required = true)
    private String city;

    @NotBlank(message = "도로명(address)는 필수입니다.")
    @Schema(description = "도로명", example = "테헤란로", required = true)
    private String roadName;

    @NotBlank(message = "건물번호(buildingNumber)는 필수입니다.")
    @Schema(description = "건물번호", example = "427", required = true)
    private String buildingNumber;

    @Size(max = 200, message = "상세주소는 최대 200자까지 입력 가능합니다.")
    @Schema(description = "상세주소 (선택)", example = "2층", required = false)
    private String detailAddress;
}
