package com.example.tiki.team.dto;

import com.example.tiki.auth.domain.User;
import com.example.tiki.team.domain.TeamRole;
import com.example.tiki.team.domain.TeamUser;
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

    private TeamRole teamRole;

    private LocalDate dateOfBirth;
    private LocalDateTime createdDate;

    public static TeamUserSimpleResponse from(User user, TeamUser teamUser) {
        return TeamUserSimpleResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .introduce(user.getIntroduce())
                .teamRole(teamUser.getTeamRole())
                .dateOfBirth(user.getDateOfBirth())
                .createdDate(teamUser.getCreatedDate())
                .build();
    }
}
