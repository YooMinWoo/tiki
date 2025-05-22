package com.example.tiki.recruitment;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.recruitment.domain.entity.Recruitment;
import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
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
public class RecruitmentDeleteTest {

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
    void 게시물_삭제_성공(){

        Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목")
                .content("테스트 내용")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        recruitmentService.deleteRecruitmentPost(leader.getId(), recruitment.getId());

        for (Recruitment rec : recruitmentRepository.findAll()) {
            assertThat(rec.getTitle()).isEqualTo(recruitment.getTitle());
            assertThat(rec.getContent()).isEqualTo(recruitment.getContent());
            assertThat(rec.getTeamId()).isEqualTo(team.getId());
            assertThat(rec.getRecruitmentStatus()).isEqualTo(RecruitmentStatus.DELETED);
        }

    }

    @Test
    void 게시물_삭제_실패_게시글존재X(){
        Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목")
                .content("테스트 내용")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> recruitmentService.deleteRecruitmentPost(leader.getId(), 100L));

        assertThat(ex.getMessage()).isEqualTo("존재하지 않는 게시글입니다.");

    }

    @Test
    void 게시물_마감_실패_게시물_이미_삭제(){
        Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목")
                .content("테스트 내용")
                .recruitmentStatus(RecruitmentStatus.CLOSE)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> recruitmentService.deleteRecruitmentPost(leader.getId(), recruitment.getId()));

        assertThat(ex.getMessage()).isEqualTo("존재하지 않는 게시글입니다.");

    }

    @Test
    void 게시물_마감_실패_리더X(){
        Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목")
                .content("테스트 내용")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class,
                () -> recruitmentService.deleteRecruitmentPost(member.getId(), recruitment.getId()));
        assertThat(forbiddenException.getMessage()).isEqualTo("해당 작업을 수행할 권한이 없습니다.");

    }

}
