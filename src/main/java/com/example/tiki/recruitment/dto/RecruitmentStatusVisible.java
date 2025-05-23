package com.example.tiki.recruitment.dto;

import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import com.example.tiki.team.domain.enums.TeamStatus;

public enum RecruitmentStatusVisible {
    OPEN,
    CLOSE;

    public RecruitmentStatus toRecruitmentStatus() {
        return RecruitmentStatus.valueOf(this.name());
    }
}
