package com.example.tiki.team;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import com.example.tiki.team.dto.*;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserHistoryRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import com.example.tiki.team.service.TeamService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class TeamServiceQueryTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamUserRepository teamUserRepository;

    @Autowired
    private TeamUserHistoryRepository teamUserHistoryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AuthRepository authRepository;

    private User leader;
    private User fake_leader;
    private User member;
    private Team team;

    @BeforeEach
    void 더미데이터_생성(){
        leader = authRepository.save(
                User.builder()
                        .name("leader")
                        .email("leader@naver.com")
                        .role(Role.ROLE_USER)
                        .build());

        member = authRepository.save(
                User.builder()
                        .name("member")
                        .email("member@naver.com")
                        .role(Role.ROLE_USER)
                        .build());

        fake_leader = authRepository.save(
                User.builder()
                        .name("fake_leader")
                        .email("fake_leader@naver.com")
                        .role(Role.ROLE_USER)
                        .build());

        team = teamRepository.save(Team.builder()
                .teamName("테스트 팀")
                .teamDescription("테스트 설명")
                .teamStatus(TeamStatus.ACTIVE)
                .build());

        teamUserRepository.save(TeamUser.builder()
                        .userId(leader.getId())
                        .teamId(team.getId())
                        .teamUserRole(TeamUserRole.ROLE_LEADER)
                        .teamUserStatus(TeamUserStatus.APPROVED)
                        .joinedAt(LocalDateTime.now())
                        .build());

    }

    @Test
    void 팀_조회(){
        for(int i=1; i <=16; i++){
            TeamStatus status = i%2==0? TeamStatus.ACTIVE : TeamStatus.INACTIVE;
            Team team = Team.builder()
                    .teamName("테스트 팀"+i)
                    .teamDescription("테스트 설명"+i)
                    .teamStatus(status)
                    .build();
            teamRepository.save(team);

            TeamUser teamUser = TeamUser.builder()
                        .teamId(team.getId())
                        .userId(leader.getId())
                        .teamUserRole(TeamUserRole.ROLE_LEADER)
                        .teamUserStatus(TeamUserStatus.APPROVED)
                        .joinedAt(LocalDateTime.now())
                        .build();

            teamUserRepository.save(teamUser);
        }
        //

        List<TeamDto> teamList1 = teamService.getTeamSearchResult(null,null);
        List<TeamDto> teamList2 = teamService.getTeamSearchResult("6",null);
        List<TeamDto> teamList3 = teamService.getTeamSearchResult(null, TeamStatusVisible.ACTIVE);
        List<TeamDto> teamList4 = teamService.getTeamSearchResult("1", TeamStatusVisible.ACTIVE);
        List<TeamDto> teamList5 = teamService.getTeamSearchResult(null, TeamStatusVisible.INACTIVE);

        assertThat(teamList1.size()).isEqualTo(17);
        assertThat(teamList2.size()).isEqualTo(2);
        assertThat(teamList3.size()).isEqualTo(9);
        assertThat(teamList4.size()).isEqualTo(4);
        assertThat(teamList5.size()).isEqualTo(8);
    }

    @Test
    void 승인_대기_리스트(){
        for(int i=1; i<=10; i++){
            User user = User.builder()
                    .name("waiting"+i)
                    .email("waiting"+i+"@naver.com")
                    .role(Role.ROLE_USER)
                    .build();

            authRepository.save(user);

            teamUserRepository.save(TeamUser.builder()
                    .userId(user.getId())
                    .teamId(team.getId())
                    .teamUserRole(null)
                    .teamUserStatus(TeamUserStatus.WAITING)
                    .joinedAt(LocalDateTime.now())
                    .build());
        }

        List<TeamUserSimpleResponse> waitingJoinRequests = teamService.getWaitingJoinRequests(leader.getId(), team.getId());

        assertThat(waitingJoinRequests.size()).isEqualTo(10);
        for (TeamUserSimpleResponse waitingJoinRequest : waitingJoinRequests) {
            System.out.println(waitingJoinRequest);
        }

    }

    @Test
    void 승인_대기_리스트_조회실패_리더X(){
        for(int i=1; i<=10; i++){
            User user = User.builder()
                    .name("waiting"+i)
                    .email("waiting"+i+"@naver.com")
                    .role(Role.ROLE_USER)
                    .build();

            authRepository.save(user);

            teamUserRepository.save(TeamUser.builder()
                    .userId(user.getId())
                    .teamId(team.getId())
                    .teamUserRole(null)
                    .teamUserStatus(TeamUserStatus.WAITING)
                    .joinedAt(LocalDateTime.now())
                    .build());
        }

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> {
            teamService.getWaitingJoinRequests(member.getId(), team.getId());
        });
        Assertions.assertThat("해당 작업을 수행할 권한이 없습니다.").isEqualTo(ex.getMessage());

    }

    // List<TeamUserSimpleResponse> getTeamUsers(Long userId, Long teamId);
    @Test
    void 회원리스트(){
        for(int i=1; i<=10; i++){
            User user = User.builder()
                    .name("member"+i)
                    .email("member"+i+"@naver.com")
                    .role(Role.ROLE_USER)
                    .build();

            authRepository.save(user);

            teamUserRepository.save(TeamUser.builder()
                    .userId(user.getId())
                    .teamId(team.getId())
                    .teamUserRole(TeamUserRole.ROLE_MEMBER)
                    .teamUserStatus(TeamUserStatus.APPROVED)
                    .joinedAt(LocalDateTime.now())
                    .build());
        }

        List<TeamUserSimpleResponse> teamUsers = teamService.getTeamUsers(member.getId(), team.getId());
        assertThat(teamUsers.size()).isEqualTo(11);

        for (TeamUserSimpleResponse teamUser : teamUsers) {
            System.out.println(teamUser);
        }
    }

    @Test
    void 없는_팀_회원리스트(){
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> teamService.getTeamUsers(member.getId(), 1000L));
        assertThat(ex.getMessage()).isEqualTo("해당 팀은 존재하지 않습니다.");
    }

    @Test
    void 내_팀_조회(){
        for(int i=1; i <=5; i++){
            Team team = Team.builder()
                    .teamName("테스트 팀"+i)
                    .teamDescription("테스트 설명"+i)
                    .teamStatus(TeamStatus.ACTIVE)
                    .build();
            teamRepository.save(team);

            TeamUser teamUser = TeamUser.builder()
                    .teamId(team.getId())
                    .userId(leader.getId())
                    .teamUserRole(TeamUserRole.ROLE_LEADER)
                    .teamUserStatus(TeamUserStatus.APPROVED)
                    .joinedAt(LocalDateTime.now())
                    .build();

            teamUserRepository.save(teamUser);
            if(i%2==0){
                teamUserRepository.save(TeamUser.builder()
                        .teamId(team.getId())
                        .userId(member.getId())
                        .teamUserRole(TeamUserRole.ROLE_MEMBER)
                        .teamUserStatus(TeamUserStatus.APPROVED)
                        .joinedAt(LocalDateTime.now())
                        .build());
            }
        }

        List<MyTeam> myTeams = teamService.getMyTeam(member.getId(), null);
        assertThat(myTeams.size()).isEqualTo(2);
        for (MyTeam myTeam : myTeams) {
            System.out.println(myTeam);

        }
    }

    @Test
    void 내_승인_대기_조회(){
        for(int i=1; i <=5; i++){
            Team team = Team.builder()
                    .teamName("테스트 팀"+i)
                    .teamDescription("테스트 설명"+i)
                    .teamStatus(TeamStatus.ACTIVE)
                    .build();
            teamRepository.save(team);

            TeamUser teamUser = TeamUser.builder()
                    .teamId(team.getId())
                    .userId(leader.getId())
                    .teamUserRole(TeamUserRole.ROLE_LEADER)
                    .teamUserStatus(TeamUserStatus.APPROVED)
                    .joinedAt(LocalDateTime.now())
                    .build();

            teamUserRepository.save(teamUser);
            if(i%2==0){
                teamUserRepository.save(TeamUser.builder()
                        .teamId(team.getId())
                        .userId(member.getId())
                        .teamUserRole(null)
                        .teamUserStatus(TeamUserStatus.WAITING)
                        .joinedAt(LocalDateTime.now())
                        .build());
            }
        }

        List<MyWaiting> myWaitings = teamService.getMyWaiting(member.getId());
        assertThat(myWaitings.size()).isEqualTo(2);
        for (MyWaiting myWaiting : myWaitings) {
            System.out.println(myWaiting);

        }
    }

    @Test
    void 팀_상세_조회(){
        // 유저 10명 생성하고
        // 팀 1개 만들고
        // 그 팀에 가입 시키고
        Team dummyTeam = teamRepository.save(Team.builder()
                .teamName("테스트 팀")
                .teamDescription("테스트 설명")
                .teamStatus(TeamStatus.ACTIVE)
                .build());
        for(int i = 1; i <= 10; i++){
            User dummyUser = authRepository.save(
                        User.builder()
                                .name("dummy"+i)
                                .email("dummy"+i+"@naver.com")
                                .role(Role.ROLE_USER)
                                .build());
            if(i == 1){
                teamUserRepository.save(TeamUser.builder()
                        .userId(dummyUser.getId())
                        .teamId(dummyTeam.getId())
                        .teamUserRole(TeamUserRole.ROLE_LEADER)
                        .teamUserStatus(TeamUserStatus.APPROVED)
                        .joinedAt(LocalDateTime.now())
                        .build());
            } else {
                teamUserRepository.save(TeamUser.builder()
                        .userId(dummyUser.getId())
                        .teamId(dummyTeam.getId())
                        .teamUserRole(TeamUserRole.ROLE_MEMBER)
                        .teamUserStatus(TeamUserStatus.APPROVED)
                        .joinedAt(LocalDateTime.now())
                        .build());
            }
        }
        TeamInfo teamInfo = teamService.getTeamInfo(dummyTeam.getId());
        assertThat(teamInfo.getLeaderName()).isEqualTo("dummy1");
        assertThat(teamInfo.getMemberCount()).isEqualTo(10);
        assertThat(teamInfo.getTeamId()).isEqualTo(dummyTeam.getId());
    }

}
