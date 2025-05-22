package com.example.tiki.recruitment;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.recruitment.domain.entity.Recruitment;
import com.example.tiki.recruitment.dto.RecruitmentCreateRequest;
import com.example.tiki.recruitment.repository.RecruitmentRepository;
import com.example.tiki.recruitment.service.RecruitmentService;
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
public class RecruitmentCreateTest {

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamUserRepository teamUserRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private NotificationRepository notificationRepository;

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

        followRepository.save(Follow.builder()
                .userId(member.getId())
                .teamId(team.getId())
                .build());

    }

    // 성공

    // 실패
    // 리더가 아닐 때

    @Test
    void 게시물_생성_성공(){
        RecruitmentCreateRequest recruitmentCreateRequest = RecruitmentCreateRequest.builder()
                                            .title("테스트 제목")
                                            .content("테스트 내용")
                                            .build();

        recruitmentService.createRecruitmentPost(leader.getId(), team.getId(), recruitmentCreateRequest);

        for (Recruitment recruitment : recruitmentRepository.findAll()) {
            assertThat(recruitment.getTitle()).isEqualTo(recruitmentCreateRequest.getTitle());
            assertThat(recruitment.getContent()).isEqualTo(recruitmentCreateRequest.getContent());
            assertThat(recruitment.getTeamId()).isEqualTo(team.getId());
            assertThat(recruitment.getOpenedAt()).isNotNull();
            assertThat(recruitment.getClosedAt()).isNull();
        }
        for (Notification notification : notificationRepository.findAll()) {
            assertThat(notification.getNotificationType()).isEqualTo(NotificationType.RECRUIT);
            assertThat(notification.getUserId()).isEqualTo(member.getId());
            System.out.println(notification.getMessage());
        }

    }

    @Test
    void 게시물_생성_실패_없는_팀(){
        RecruitmentCreateRequest recruitmentCreateRequest = RecruitmentCreateRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> recruitmentService.createRecruitmentPost(leader.getId(), 100L, recruitmentCreateRequest));

        assertThat(ex.getMessage()).isEqualTo("해당 팀은 존재하지 않습니다.");

    }

    @Test
    void 게시물_생성_실패_리더X(){
        RecruitmentCreateRequest recruitmentCreateRequest = RecruitmentCreateRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class,
                () -> recruitmentService.createRecruitmentPost(member.getId(), team.getId(), recruitmentCreateRequest));
        assertThat(forbiddenException.getMessage()).isEqualTo("해당 작업을 수행할 권한이 없습니다.");

    }

}
