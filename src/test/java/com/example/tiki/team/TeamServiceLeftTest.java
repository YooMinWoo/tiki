package com.example.tiki.team;


import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.TeamApplicationException;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.entity.TeamUserHistory;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserHistoryRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import com.example.tiki.team.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class TeamServiceLeftTest {

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

        team = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀")
                        .teamDescription("테스트 설명")
                        .teamStatus(TeamStatus.ACTIVE)
                        .build());

        teamUserRepository.save(TeamUser.builder()
                .userId(leader.getId())
                .teamId(team.getId())
                .teamUserRole(TeamUserRole.ROLE_LEADER)
                .teamUserStatus(TeamUserStatus.APPROVED)
                .build());

        teamUserRepository.save(TeamUser.builder()
                .userId(fake_leader.getId())
                .teamId(team.getId())
                .teamUserRole(TeamUserRole.ROLE_MEMBER)
                .teamUserStatus(TeamUserStatus.APPROVED)
                .build());
    }

    // 성공

    // 실패
    // 1. 해당 팀 소속이 아닐 때
    // 2. 권한이 리더일 때

    // 팀 소속인지 확인
    //        TeamUser teamUser = getTeamUserWithStatus(user.getId(), teamId, TeamUserStatus.APPROVED);
    //
    //        // if 권한이 리더 -> throw
    //        if(teamUser.getTeamUserRole() == TeamUserRole.ROLE_LEADER){
    //            throw new TeamApplicationException("감독을 다른 사람에게 위임한 뒤 탈퇴하여 주세요.");
    //        }

    @Test
    void 탈퇴_성공(){
        TeamUser teamUser = TeamUser.builder()
                .userId(member.getId())
                .teamId(team.getId())
                .teamUserRole(TeamUserRole.ROLE_MEMBER)
                .teamUserStatus(TeamUserStatus.APPROVED)
                .build();

        teamUserRepository.save(teamUser);

        teamService.requestTeamLeave(member, team.getId());

        List<Notification> notifications = notificationRepository.findByUserId(leader.getId());
        TeamUserHistory teamUserHistory = teamUserHistoryRepository.findAll().get(0);

        assertThat(teamUser.getTeamUserRole()).isEqualTo(TeamUserRole.ROLE_MEMBER);
        assertThat(teamUser.getTeamUserStatus()).isEqualTo(TeamUserStatus.LEFT);

        assertThat(notifications.get(0).getNotificationType()).isEqualTo(NotificationType.LEFT);

        assertThat(teamUserHistory.getUserId()).isEqualTo(member.getId());
        assertThat(teamUserHistory.getCurrentStatus()).isEqualTo(TeamUserStatus.LEFT);
        assertThat(teamUserHistory.getCurrentRole()).isEqualTo(TeamUserRole.ROLE_MEMBER);
        assertThat(teamUserHistory.getPreviousStatus()).isEqualTo(TeamUserStatus.APPROVED);
        assertThat(teamUserHistory.getPreviousRole()).isEqualTo(TeamUserRole.ROLE_MEMBER);
    }


    // 실패
    // 1. 해당 팀 소속이 아닐 때
    // 2. 권한이 리더일 때
    @Test
    void 탈퇴_실패_팀원X(){
        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> {
            teamService.requestTeamLeave(member, team.getId());
        });
        assertThat("해당 상태의 유저를 찾을 수 없습니다.").isEqualTo(ex.getMessage());
    }

    @Test
    void 탈퇴_실패_리더는_탈퇴_불가능(){
        TeamApplicationException ex = assertThrows(TeamApplicationException.class, () -> {
            teamService.requestTeamLeave(leader, team.getId());
        });
        assertThat("감독을 다른 사람에게 위임한 뒤 탈퇴하여 주세요.").isEqualTo(ex.getMessage());
    }
}
