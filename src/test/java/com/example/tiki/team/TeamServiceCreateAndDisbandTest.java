package com.example.tiki.team;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.entity.TeamUserHistory;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import com.example.tiki.team.dto.TeamCreateRequestDto;
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

@SpringBootTest
@Transactional
public class TeamServiceCreateAndDisbandTest {

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

    @Autowired
    NotificationRepository notificationRepository;


    private User leader;


    @BeforeEach
    void 멤버_생성(){
        leader = authRepository.save(
                User.builder()
                        .name("leader")
                        .email("leader@naver.com")
                        .role(Role.ROLE_USER)
                        .build());

    }

    @Test
    void 팀_생성_성공() {
        Long userId = leader.getId();
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

    @Test
    void 팀_해체_성공(){
        // 리더, 멤버, 대기중인 유저, 팀
        // given
        User approved_member = authRepository.save(
                        User.builder()
                                .name("approved_member")
                                .email("approved_member@naver.com")
                                .role(Role.ROLE_USER)
                                .build());
        User waiting_member = authRepository.save(
                        User.builder()
                                .name("waiting_member")
                                .email("waiting_member@naver.com")
                                .role(Role.ROLE_USER)
                                .build());

        Team team = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀")
                        .teamDescription("테스트 설명")
                        .teamStatus(TeamStatus.ACTIVE)
                        .build());

        teamUserRepository.save(TeamUser.builder()
                        .userId(approved_member.getId())
                        .teamId(team.getId())
                        .teamUserRole(TeamUserRole.ROLE_MEMBER)
                        .teamUserStatus(TeamUserStatus.APPROVED)
                        .build());
        teamUserRepository.save(TeamUser.builder()
                .userId(leader.getId())
                .teamId(team.getId())
                .teamUserRole(TeamUserRole.ROLE_LEADER)
                .teamUserStatus(TeamUserStatus.APPROVED)
                .build());
        teamUserRepository.save(TeamUser.builder()
                .userId(waiting_member.getId())
                .teamId(team.getId())
                .teamUserRole(null)
                .teamUserStatus(TeamUserStatus.WAITING)
                .build());

        // when
        teamService.disbandTeam(leader.getId(), team.getId());

        // then
        // 팀 상태 변경 되었는지 확인
        assertThat(team.getTeamStatus()).isEqualTo(TeamStatus.DISBANDED);

        for (TeamUser teamUser : teamUserRepository.findAll()) {
            // teamUser에서 팀원들의 status 확인
            assertThat(teamUser.getTeamUserStatus()).isEqualTo(TeamUserStatus.DISBANDED);
            System.out.println(teamUser.toString());

            // 알림 확인
            List<Notification> notifications = notificationRepository.findByUserId(teamUser.getUserId());
            assertThat(notifications.size()).isEqualTo(1);
            assertThat(notifications.get(0).getNotificationType()).isEqualTo(NotificationType.DISBAND);
            assertThat(notifications.get(0).getUserId()).isEqualTo(teamUser.getUserId());
            assertThat(notifications.get(0).getTargetId()).isEqualTo(teamUser.getTeamId());
            System.out.println(notifications.get(0).toString());

            // history 확인
            List<TeamUserHistory> teamUserHistories = teamUserHistoryRepository.findByUserId(teamUser.getUserId());
            assertThat(teamUserHistories.size()).isEqualTo(1);
            assertThat(teamUserHistories.get(0).getTeamId()).isEqualTo(teamUser.getTeamId());
            assertThat(teamUserHistories.get(0).getUserId()).isEqualTo(teamUser.getUserId());
            assertThat(teamUserHistories.get(0).getCurrentStatus()).isEqualTo(teamUser.getTeamUserStatus());
            System.out.println(teamUserHistories.get(0).toString());
        }
    }

}
