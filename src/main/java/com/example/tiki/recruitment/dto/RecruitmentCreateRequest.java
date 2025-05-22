package com.example.tiki.recruitment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecruitmentCreateRequest {
    private String title;
    private String content;
}
