package com.example.tiki.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateNoticeRequest {

    @Schema(description = "공지사항 제목 (10~30자)", example = "시스템 점검 안내", required = true)
    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 10, max = 30, message = "제목은 10자 이상 30자 이하로 입력해야 합니다.")
    private String title;

    @Schema(description = "공지사항 내용 (30~1000자)", example = "시스템 점검으로 인해 2025년 6월 3일 00:00~02:00 사이에 서비스가 중단됩니다.", required = true)
    @NotBlank(message = "내용은 필수입니다.")
    @Size(min = 30, max = 1000, message = "내용은 30자 이상 1000자 이하로 입력해야 합니다.")
    private String content;

}
