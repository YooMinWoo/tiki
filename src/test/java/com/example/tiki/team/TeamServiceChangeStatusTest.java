package com.example.tiki.team;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.recruitment.domain.entity.Recruitment;
import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import com.example.tiki.recruitment.repository.RecruitmentRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class TeamServiceChangeStatusTest {

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

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    private User leader;
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
    }

    @Test
    void 팀_비활성화(){

        Recruitment recruitment1 = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목1")
                .content("테스트 내용1")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        Recruitment recruitment2 = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목2")
                .content("테스트 내용2")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        teamService.inactiveTeam(leader.getId(), team.getId());



        assertThat(team.getTeamStatus()).isEqualTo(TeamStatus.INACTIVE);
        assertThat(recruitment1.getRecruitmentStatus()).isEqualTo(RecruitmentStatus.CLOSE);
        assertThat(recruitment2.getRecruitmentStatus()).isEqualTo(RecruitmentStatus.CLOSE);
        assertThat(recruitment1.getClosedAt()).isNotNull();
        assertThat(recruitment2.getClosedAt()).isNotNull();

    }

    @Test
    void 팀_활성화(){
        Recruitment recruitment1 = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목1")
                .content("테스트 내용1")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        Recruitment recruitment2 = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목2")
                .content("테스트 내용2")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        teamService.inactiveTeam(leader.getId(), team.getId());
        teamService.activeTeam(leader.getId(), team.getId());



        assertThat(team.getTeamStatus()).isEqualTo(TeamStatus.ACTIVE);

    }
}
