package com.example.tiki.team;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.init.UserInit;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.dto.NotificationDto;
import com.example.tiki.notifircation.service.NotificationService;
import com.example.tiki.team.domain.Team;
import com.example.tiki.team.domain.TeamRole;
import com.example.tiki.team.domain.TeamStatus;
import com.example.tiki.team.domain.TeamUser;
import com.example.tiki.team.dto.TeamCreateRequestDto;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import com.example.tiki.team.service.TeamService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
    AuthRepository authRepository;

    @Autowired
    UserInit userInit;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamUserRepository teamUserRepository;

    @Autowired
    NotificationService notificationService;

    @BeforeEach
    void beforeEach(){
        // 유저, 팀
        for(int i=1; i<=10; i++){
            authRepository.save(
                    User.builder()
                            .name("test"+i)
                            .email("test"+i+"@test.com")
                            .role(Role.ROLE_USER)
                            .build());
        }

        teamRepository.save(Team.builder()
                .teamName("test FC")
                .teamDescription("test FC입니다.")
                .teamStatus(TeamStatus.OPEN)
                .build());

        teamUserRepository.save(TeamUser.builder()
                .userId(1L)
                .teamId(1L)
                .teamRole(TeamRole.ROLE_LEADER)
                .build());
    }

//    @Test
//    public void createTeam(){
//        // given
//        userInit.userSignup();
//        TeamCreateRequestDto teamCreateRequestDto = TeamCreateRequestDto.builder()
//                .teamName("푸에르 FC")
//                .teamDescription("푸에르 FC입니다.")
//                .build();
//
//        // when
//        teamService.createTeam(1L, teamCreateRequestDto);
//
//        // then
//        List<Team> teams = teamRepository.findAll();
//        List<TeamUser> teamUsers = teamUserRepository.findAll();
//
//        assertEquals(1, teams.size());
//        assertEquals("푸에르 FC", teams.get(0).getTeamName());
//
//        assertEquals(1, teamUsers.size());
//        assertEquals(1L, teamUsers.get(0).getUserId());
//        assertEquals(TeamRole.ROLE_LEADER, teamUsers.get(0).getTeamRole());
//    }
//
//    @Test
//    public void 가입신청(){
//        // given
//        userInit.userSignup();
//        TeamCreateRequestDto teamCreateRequestDto = TeamCreateRequestDto.builder()
//                .teamName("푸에르 FC")
//                .teamDescription("푸에르 FC입니다.")
//                .build();
//        teamService.createTeam(1L, teamCreateRequestDto);
//
//        User user = authRepository.findById(2L).get();
//        Long teamId = 1L;
//
//        // when
//        TeamUser teamUser = teamService.teamJoinRequest(user, teamId);
//
//        // then
//        List<NotificationDto> notificationDtos = notificationService.getNotification(2L);
//        for(NotificationDto dto : notificationDtos){
//            assertSame(dto.getTargetId(), user.getId());
//            assertEquals(dto.getMessage(), user.getName()+"님께서 가입 요청을 보냈습니다.");
//        }
//
//        assertEquals(user.getId(), teamUser.getUserId());
//        assertEquals(teamId, teamUser.getTeamId());
//        assertEquals(TeamRole.ROLE_WAITING, teamUser.getTeamRole());  // 가입 요청 상태는 ROLE_WATTING이어야 한다
//        assertNotNull(teamUser.getCreatedDate());  // 생성일자가 null이 아니어야 한다
//    }

    @Test
    void 가입수락(){
//        for(int i=1; i<=10; i++){
//            authRepository.save(
//                    User.builder()
//                            .name("test"+i)
//                            .email("test"+i+"@test.com")
//                            .role(Role.ROLE_USER)
//                            .build());
//        }
//
//        teamRepository.save(Team.builder()
//                .teamName("test FC")
//                .teamDescription("test FC입니다.")
//                .teamStatus(TeamStatus.OPEN)
//                .build());
//
//        teamUserRepository.save(TeamUser.builder()
//                .userId(1L)
//                .teamId(1L)
//                .teamRole(TeamRole.ROLE_LEADER)
//                .build());
        // given
        for(User user : authRepository.findAll()){
            System.out.println(user.getId());
        }
        User user = authRepository.findById(2L).get();

        // 가입신청
        teamService.teamJoinRequest(user, 1L);

        // 가입수락
        teamService.approveTeamJoinRequest(1L, 2L, 1L); //Long leaderId, Long userId, Long teamId

        // 결과
        for (NotificationDto dto : notificationService.getNotification(2L)) {
            assertEquals(dto.getMessage(), "test FC팀에서 가입을 수락했습니다.");
            assertEquals(dto.getTargetId(), 1L);
        }

    }

    @Test
    void 가입거절(){
//        for(int i=1; i<=10; i++){
//            authRepository.save(
//                    User.builder()
//                            .name("test"+i)
//                            .email("test"+i+"@test.com")
//                            .role(Role.ROLE_USER)
//                            .build());
//        }
//
//        teamRepository.save(Team.builder()
//                .teamName("test FC")
//                .teamDescription("test FC입니다.")
//                .teamStatus(TeamStatus.OPEN)
//                .build());
//
//        teamUserRepository.save(TeamUser.builder()
//                .userId(1L)
//                .teamId(1L)
//                .teamRole(TeamRole.ROLE_LEADER)
//                .build());
        // given
        for(User user : authRepository.findAll()){
            System.out.println(user.getId());
        }
        User user = authRepository.findById(2L).get();

        // 가입신청
        teamService.teamJoinRequest(user, 1L);

        // 가입거절
        teamService.rejectTeamJoinRequest(1L, 2L, 1L); //Long leaderId, Long userId, Long teamId

        // 결과
        for (NotificationDto dto : notificationService.getNotification(2L)) {
            assertEquals(dto.getMessage(), "test FC팀에서 가입을 거절했습니다.");
            assertEquals(dto.getTargetId(), 1L);
        }

    }
}