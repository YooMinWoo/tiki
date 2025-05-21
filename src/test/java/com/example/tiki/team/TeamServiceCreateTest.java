package com.example.tiki.team;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.entity.TeamUserHistory;
import com.example.tiki.team.dto.TeamCreateRequestDto;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserHistoryRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import com.example.tiki.team.service.TeamService;
import com.example.tiki.team.service.TeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class TeamServiceCreateTest {

    @Autowired
    TeamService teamService;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamUserRepository teamUserRepository;

    @Autowired
    TeamUserHistoryRepository teamUserHistoryRepository;

    @Autowired
    AuthRepository authRepository;


    private User user;

    @BeforeEach
    void 멤버_생성(){
        user = authRepository.save(
                User.builder()
                        .name("leader")
                        .email("leader@naver.com")
                        .role(Role.ROLE_USER)
                        .build());
    }

    @Test
    void 팀_생성_성공() {
        Long userId = user.getId();
        TeamCreateRequestDto requestDto = new TeamCreateRequestDto("IntegrationTestTeam", "팀 설명");

        teamService.createTeam(userId, requestDto);

        List<Team> teams = teamRepository.findAll();
        assertThat(teams).isNotEmpty();

        Team team = teams.get(0);
        assertThat(team.getTeamName()).isEqualTo(requestDto.getTeamName());

        List<TeamUser> teamUsers = teamUserRepository.findAll();
        assertThat(teamUsers).isNotEmpty();

        List<TeamUserHistory> histories = teamUserHistoryRepository.findAll();
        assertThat(histories).isNotEmpty();
    }

}
