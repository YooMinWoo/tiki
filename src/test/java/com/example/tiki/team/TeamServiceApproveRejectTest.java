package com.example.tiki.team;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.global.exception.ForbiddenException;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class TeamServiceApproveRejectTest {
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
    // 1. 이미 팀원일 때 (현재 상태가 approved임)
    // 2. 지원하지 않았을 경우 (TeamUser가 null임)
    // 3. 리더가 아닐 경우 (leaderId가 fake_leader)
    @Test
    void 승인_성공(){
        teamUserRepository.save(TeamUser.builder()
                .userId(member.getId())
                .teamId(team.getId())
                .teamUserRole(null)
                .teamUserStatus(TeamUserStatus.WAITING)
                .build());

        teamService.handleTeamUserAction(leader.getId(), member.getId(), team.getId(), TeamUserStatus.APPROVED);

        TeamUser teamUser = teamUserRepository.findByUserIdAndTeamId(member.getId(), team.getId()).get();
        List<Notification> notifications = notificationRepository.findByUserId(member.getId());
        TeamUserHistory teamUserHistory = teamUserHistoryRepository.findAll().get(0);

        assertThat(teamUser.getTeamUserRole()).isEqualTo(TeamUserRole.ROLE_MEMBER);
        assertThat(teamUser.getTeamUserStatus()).isEqualTo(TeamUserStatus.APPROVED);
        assertNotNull(teamUser.getJoinedAt());

        assertThat(notifications.get(0).getNotificationType()).isEqualTo(NotificationType.APPROVE);

        assertThat(teamUserHistory.getUserId()).isEqualTo(member.getId());
        assertThat(teamUserHistory.getCurrentStatus()).isEqualTo(teamUser.getTeamUserStatus());
        assertThat(teamUserHistory.getCurrentRole()).isEqualTo(teamUser.getTeamUserRole());
        assertThat(teamUserHistory.getPreviousStatus()).isEqualTo(TeamUserStatus.WAITING);
        assertThat(teamUserHistory.getPreviousRole()).isEqualTo(null);
    }

    @Test
    void 거절_성공(){
        TeamUser teamUser = TeamUser.builder()
                    .userId(member.getId())
                    .teamId(team.getId())
                    .teamUserRole(null)
                    .teamUserStatus(TeamUserStatus.WAITING)
                    .build();
        teamUserRepository.save(teamUser);

        teamService.handleTeamUserAction(leader.getId(), member.getId(), team.getId(), TeamUserStatus.REJECTED);

        List<Notification> notifications = notificationRepository.findByUserId(member.getId());
        TeamUserHistory teamUserHistory = teamUserHistoryRepository.findAll().get(0);

        assertThat(teamUser.getTeamUserRole()).isEqualTo(null);
        assertThat(teamUser.getTeamUserStatus()).isEqualTo(TeamUserStatus.REJECTED);
        assertNotNull(teamUser.getCreatedDate());

        assertThat(notifications.get(0).getNotificationType()).isEqualTo(NotificationType.REJECT);

        assertThat(teamUserHistory.getUserId()).isEqualTo(member.getId());
        assertThat(teamUserHistory.getCurrentStatus()).isEqualTo(TeamUserStatus.REJECTED);
        assertThat(teamUserHistory.getCurrentRole()).isEqualTo(null);
        assertThat(teamUserHistory.getPreviousStatus()).isEqualTo(TeamUserStatus.WAITING);
        assertThat(teamUserHistory.getPreviousRole()).isEqualTo(null);
    }

    @Test
    void 승인_실패_지원X(){
        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> {
            teamService.handleTeamUserAction(leader.getId(), member.getId(), team.getId(), TeamUserStatus.APPROVED);
        });
        assertThat("해당 상태의 유저를 찾을 수 없습니다.").isEqualTo(ex.getMessage());
    }

    @Test
    void 거절_실패_지원X(){
        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> {
            teamService.handleTeamUserAction(leader.getId(), member.getId(), team.getId(), TeamUserStatus.REJECTED);
        });
        assertThat("해당 상태의 유저를 찾을 수 없습니다.").isEqualTo(ex.getMessage());
    }

    @Test
    void 승인_실패_리더X(){
        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> {
            teamService.handleTeamUserAction(fake_leader.getId(), member.getId(), team.getId(), TeamUserStatus.APPROVED);
        });
        assertThat("해당 작업을 수행할 권한이 없습니다.").isEqualTo(ex.getMessage());
    }

    @Test
    void 거절_실패_리더X(){
        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> {
            teamService.handleTeamUserAction(fake_leader.getId(), member.getId(), team.getId(), TeamUserStatus.REJECTED);
        });
        assertThat("해당 작업을 수행할 권한이 없습니다.").isEqualTo(ex.getMessage());
    }
}
