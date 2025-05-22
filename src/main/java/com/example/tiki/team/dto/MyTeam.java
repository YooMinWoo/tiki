package com.example.tiki.team.dto;

import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MyTeam {
    // 팀 id
    // 팀 이름
    // 내 권한
    // 팀원 수
    // 가입일자
    // 팀 생성일자
    private Long teamId;
    private String teamName;
    private TeamUserRole teamUserRole;
    private int memberCount;
    private TeamStatus teamStatus;
    private LocalDateTime joinedAt;
    private LocalDateTime createDate;

    public static MyTeam from(Team team, int memberCount, TeamUser teamUser){
        return MyTeam.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .teamUserRole(teamUser.getTeamUserRole())
                .memberCount(memberCount)
                .teamStatus(team.getTeamStatus())
                .joinedAt(teamUser.getJoinedAt())
                .createDate(team.getCreatedDate())
                .build();
    }
}
