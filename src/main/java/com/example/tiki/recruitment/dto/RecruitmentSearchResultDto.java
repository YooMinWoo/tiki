package com.example.tiki.recruitment.dto;

import com.example.tiki.recruitment.domain.entity.Recruitment;
import com.example.tiki.team.domain.entity.Team;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class RecruitmentSearchResultDto {

    private Long recruitmentId;
    private Long teamId;
    private String title;
    private String teamName;
    private LocalDateTime openedAt;

    public static RecruitmentSearchResultDto from(Recruitment recruitment, Team team){
        return RecruitmentSearchResultDto.builder()
                    .recruitmentId(recruitment.getId())
                    .teamId(team.getId())
                    .title(recruitment.getTitle())
                    .teamName(team.getTeamName())
                    .openedAt(recruitment.getOpenedAt())
                    .build();
    }
}
