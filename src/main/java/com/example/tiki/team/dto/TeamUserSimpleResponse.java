package com.example.tiki.team.dto;

import com.example.tiki.auth.domain.User;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
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

//    private TeamUserStatus teamUserStatus;
    private TeamUserRole teamUserRole;

    private LocalDate dateOfBirth;
    private LocalDateTime createdDate;

    public static TeamUserSimpleResponse from(User user, TeamUser teamUser) {
        return TeamUserSimpleResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .introduce(user.getIntroduce())
                .teamUserRole(teamUser.getTeamUserRole())
                .dateOfBirth(user.getDateOfBirth())
                .createdDate(teamUser.getCreatedDate())
                .build();
    }
}
