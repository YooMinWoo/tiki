package com.example.tiki.team.dto;

import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class MyWaiting {

    // teamId
    // 팀 이름
    // 현재 상태
    // 지원 날짜

    private Long teamId;
    private String teamName;
    private TeamUserStatus teamUserStatus;
    private LocalDateTime requestAt;

    public static MyWaiting from(Team team, TeamUser teamUser){
        return MyWaiting.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .teamUserStatus(teamUser.getTeamUserStatus())
                .requestAt(teamUser.getCreatedDate())
                .build();
    }
}
