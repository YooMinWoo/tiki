package com.example.tiki.team.dto;

import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.enums.TeamStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamDto {

    private Long teamId;

    private String teamName;
    private String teamDescription;
    private TeamStatus teamStatus;

    public static TeamDto toDto(Team team){
        return TeamDto.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .teamDescription(team.getTeamDescription())
                .teamStatus(team.getTeamStatus())
                .build();
    }
}
