package com.example.tiki.team.dto;

import com.example.tiki.auth.domain.User;
import com.example.tiki.team.domain.TeamRole;
import com.example.tiki.team.domain.TeamStatus;
import com.example.tiki.team.domain.TeamUser;
import com.example.tiki.team.domain.TeamUserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TeamUserSimpleResponse {

    private Long userId;
    private String name;
    private String email;
    private String introduce;

    private TeamUserStatus teamUserStatus;

    private LocalDate dateOfBirth;
    private LocalDateTime createdDate;

    public static TeamUserSimpleResponse from(User user, TeamUser teamUser) {
        return TeamUserSimpleResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .introduce(user.getIntroduce())
                .teamUserStatus(teamUser.getTeamUserStatus())
                .dateOfBirth(user.getDateOfBirth())
                .createdDate(teamUser.getCreatedDate())
                .build();
    }
}
