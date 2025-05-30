package com.example.tiki.recruitment.dto;

import com.example.tiki.recruitment.domain.entity.Recruitment;
import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import com.example.tiki.recruitment.domain.enums.RecruitmentType;
import com.example.tiki.team.domain.entity.Team;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecruitmentDetailDto {
    /*
    private Long id;

    private Long teamId;

    private String title;

    private String content;

//    @Enumerated(EnumType.STRING)
//    private RecruitmentType recruitmentType; // MEMBER or MERCENARY

    @Enumerated(EnumType.STRING)
    private RecruitmentStatus recruitmentStatus; // OPEN, CLOSED, DELETED

    private LocalDateTime openedAt;

    private LocalDateTime closedAt;
     */
    private Long recruitmentId;
    private Long teamId;
    private String teamName;
    private String title;
    private String content;
    private RecruitmentStatus recruitmentStatus;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    public static RecruitmentDetailDto from(Recruitment recruitment, Team team){
        return RecruitmentDetailDto.builder()
                    .recruitmentId(recruitment.getId())
                    .teamId(team.getId())
                    .teamName(team.getTeamName())
                    .title(recruitment.getTitle())
                    .content(recruitment.getContent())
                    .recruitmentStatus(recruitment.getRecruitmentStatus())
                    .openedAt(recruitment.getOpenedAt())
                    .closedAt(recruitment.getClosedAt())
                    .build();
    }
}
