package com.example.tiki.team;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TeamServiceLeaderChangeTest {

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
    private User member;
    private User member2;
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

        member = authRepository.save(
                User.builder()
                        .name("member2")
                        .email("member2@naver.com")
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
                .userId(member.getId())
                .teamId(team.getId())
                .teamUserRole(TeamUserRole.ROLE_MEMBER)
                .teamUserStatus(TeamUserStatus.APPROVED)
                .build());
    }

    @Test
    void 팀_리더_변경_성공() {
        teamService.changeLeader(leader.getId(), member.getId(), team.getId());
        TeamUser teamUser = teamUserRepository.findByTeamIdAndTeamUserRole(team.getId(), TeamUserRole.ROLE_LEADER);
        TeamUser teamUser2 = teamUserRepository.findByTeamIdAndTeamUserRole(team.getId(), TeamUserRole.ROLE_MEMBER);

        assertThat(teamUser.getUserId()).isEqualTo(member.getId());
        assertThat(teamUser2.getUserId()).isEqualTo(leader.getId());

        List<TeamUserHistory> all = teamUserHistoryRepository.findAll();
        for (TeamUserHistory teamUserHistory : all) {
            System.out.println(teamUserHistory);
        }
    }

    @Test
    void 팀_가입_요청_실패_이미대기() {
        // history, notification 확인
        teamUserRepository.save(TeamUser.builder()
                .userId(member.getId())
                .teamId(team.getId())
                .teamUserRole(null)
                .teamUserStatus(TeamUserStatus.WAITING)
                .build());

        TeamApplicationException ex = assertThrows(TeamApplicationException.class, () -> {
            teamService.teamJoinRequest(member, team.getId());
        });
        assertThat("이미 지원한 팀입니다.").isEqualTo(ex.getMessage());

    }

    @Test
    void 팀_가입_요청_실패_이미가입됨() {
        teamUserRepository.save(TeamUser.builder()
                .userId(member.getId())
                .teamId(team.getId())
                .teamUserRole(TeamUserRole.ROLE_MEMBER)
                .teamUserStatus(TeamUserStatus.APPROVED)
                .build());

        TeamApplicationException ex = assertThrows(TeamApplicationException.class, () -> {
            teamService.teamJoinRequest(member, team.getId());
        });
        assertThat("이미 소속된 팀입니다.").isEqualTo(ex.getMessage());
    }

    @Test
    void 팀_가입_요청_실패_팀_없음() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            teamService.teamJoinRequest(member, 100L);
        });
        assertThat("존재하지 않는 팀입니다.").isEqualTo(ex.getMessage());
    }

    @Test
    void 가입_요청_취소_성공() {
        TeamUser joinRequest = teamService.teamJoinRequest(member, team.getId());
        teamService.cancelJoinRequest(member.getId(), team.getId());

        Optional<TeamUser> deleted = teamUserRepository.findById(joinRequest.getId());
        assertTrue(deleted.isEmpty());

        List<TeamUserHistory> histories = teamUserHistoryRepository.findAll();
        assertTrue(histories.isEmpty());
    }

    @Test
    void 가입_요청_취소_실패_대기_상태_아님() {
        TeamUser approvedUser = TeamUser.builder()
                .userId(member.getId())
                .teamId(team.getId())
                .teamUserRole(TeamUserRole.ROLE_MEMBER)
                .teamUserStatus(TeamUserStatus.APPROVED)
                .build();
        teamUserRepository.save(approvedUser);

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class, () -> {
            teamService.cancelJoinRequest(approvedUser.getUserId(), team.getId());
        });
        assertThat(forbiddenException.getMessage()).isEqualTo("해당 상태의 유저를 찾을 수 없습니다.");
    }


}
