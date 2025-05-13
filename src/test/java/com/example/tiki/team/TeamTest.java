package com.example.tiki.team;

import com.example.tiki.init.UserInit;
import com.example.tiki.team.domain.Team;
import com.example.tiki.team.domain.TeamRole;
import com.example.tiki.team.domain.TeamUser;
import com.example.tiki.team.dto.TeamCreateRequestDto;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import com.example.tiki.team.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamTest {

    @Autowired
    TeamService teamService;

    @Autowired
    UserInit userInit;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamUserRepository teamUserRepository;

    @Test
    public void createTeam(){
        // given
        userInit.userSignup();
        TeamCreateRequestDto teamCreateRequestDto = TeamCreateRequestDto.builder()
                .teamName("푸에르 FC")
                .teamDescription("푸에르 FC입니다.")
                .build();

        // when
        teamService.createTeam(1L, teamCreateRequestDto);

        // then
        List<Team> teams = teamRepository.findAll();
        List<TeamUser> teamUsers = teamUserRepository.findAll();

        assertEquals(1, teams.size());
        assertEquals("푸에르 FC", teams.get(0).getTeamName());

        assertEquals(1, teamUsers.size());
        assertEquals(1L, teamUsers.get(0).getUserId());
        assertEquals(TeamRole.ROLE_LEADER, teamUsers.get(0).getTeamRole());
    }

    @Test
    public void joinTeam(){
        // given
        userInit.userSignup();
        TeamCreateRequestDto teamCreateRequestDto = TeamCreateRequestDto.builder()
                .teamName("푸에르 FC")
                .teamDescription("푸에르 FC입니다.")
                .build();
        teamService.createTeam(1L, teamCreateRequestDto);

        Long userId = 2L;
        Long teamId = 1L;

        // when
        TeamUser teamUser = teamService.teamJoinRequest(userId, teamId);

        // then
        assertEquals(userId, teamUser.getUserId());
        assertEquals(teamId, teamUser.getTeamId());
        assertEquals(TeamRole.ROLE_WAITING, teamUser.getTeamRole());  // 가입 요청 상태는 ROLE_WATTING이어야 한다
        assertNotNull(teamUser.getCreatedDate());  // 생성일자가 null이 아니어야 한다
    }
}