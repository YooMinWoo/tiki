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
import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import com.example.tiki.recruitment.dto.RecruitmentCreateRequest;
import com.example.tiki.recruitment.dto.RecruitmentUpdateRequest;
import com.example.tiki.recruitment.repository.RecruitmentRepository;
import com.example.tiki.recruitment.service.RecruitmentService;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class RecruitmentUpdateTest {

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
    // 없는 팀일 때
    // 리더가 아닐 때

    @Test
    void 게시물_수정_성공(){

        Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목")
                .content("테스트 내용")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        LocalDateTime beforeDate = recruitment.getLastModifiedDate();

        RecruitmentUpdateRequest request = RecruitmentUpdateRequest.builder()
                .recruitmentId(recruitment.getId())
                .title("변경된 title")
                .content("변경된 content")
                .build();
        recruitmentService.updateRecruitmentPost(leader.getId(), request);

        for (Recruitment rec : recruitmentRepository.findAll()) {
            assertThat(rec.getTitle()).isEqualTo(request.getTitle());
            assertThat(rec.getContent()).isEqualTo(request.getContent());
            assertThat(rec.getTeamId()).isEqualTo(team.getId());
            assertThat(rec.getOpenedAt()).isNotNull();
            assertThat(rec.getClosedAt()).isNull();
            assertThat(beforeDate).isNotEqualTo(rec.getLastModifiedDate());
        }

    }

    @Test
    void 게시물_수정_실패_게시글존재X(){
        Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목")
                .content("테스트 내용")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        RecruitmentUpdateRequest request = RecruitmentUpdateRequest.builder()
                .recruitmentId(100L)
                .title("변경된 title")
                .content("변경된 content")
                .build();

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> recruitmentService.updateRecruitmentPost(leader.getId(), request));

        assertThat(ex.getMessage()).isEqualTo("존재하지 않는 게시글입니다.");

    }

    @Test
    void 게시물_수정_실패_리더X(){
        Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목")
                .content("테스트 내용")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        RecruitmentUpdateRequest request = RecruitmentUpdateRequest.builder()
                .recruitmentId(recruitment.getId())
                .title("변경된 title")
                .content("변경된 content")
                .build();

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class,
                () -> recruitmentService.updateRecruitmentPost(member.getId(), request));
        assertThat(forbiddenException.getMessage()).isEqualTo("해당 작업을 수행할 권한이 없습니다.");

    }

}
