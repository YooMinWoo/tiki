package com.example.tiki.team.dto;

import com.example.tiki.auth.domain.User;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.enums.TeamStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TeamInfo {

    // 팀 상세 페이지 dto, 내 팀들 리스트 보여줄 때 나타나는 dto는 MyTeam을 활용

    private Long teamId;
    private String teamName;
    private String teamDescription;
    private TeamStatus teamStatus;
    private LocalDateTime createDate;

    private int memberCount;

    private Long leaderId;
    private String leaderName;

    public static TeamInfo from(Team team, int memberCount, User user){
        return TeamInfo.builder()
                    .teamId(team.getId())
                    .teamName(team.getTeamName())
                    .teamDescription(team.getTeamDescription())
                    .teamStatus(team.getTeamStatus())
                    .createDate(team.getCreatedDate())
                    .memberCount(memberCount)
                    .leaderId(user.getId())
                    .leaderName(user.getName())
                    .build();
    }
}
