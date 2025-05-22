package com.example.tiki.recruitment.dto;

import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecruitmentDto {

    private Long recruitmentId;

    private Long teamId;

    private Long writerId;

    private String title;

    private String content;

//    @Enumerated(EnumType.STRING)
//    private RecruitmentType recruitmentType; // MEMBER or MERCENARY

    @Enumerated(EnumType.STRING)
    private RecruitmentStatus recruitmentStatus; // OPEN, CLOSED, DELETED

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}
