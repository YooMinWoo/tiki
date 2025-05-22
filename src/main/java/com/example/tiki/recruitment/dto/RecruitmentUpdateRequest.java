package com.example.tiki.recruitment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecruitmentUpdateRequest {
    private Long recruitmentId;
    private String title;
    private String content;
}
