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
import com.example.tiki.recruitment.dto.RecruitmentCreateRequest;
import com.example.tiki.recruitment.dto.RecruitmentDetailDto;
import com.example.tiki.recruitment.dto.RecruitmentSearchResultDto;
import com.example.tiki.recruitment.dto.RecruitmentStatusVisible;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class RecuritmentSearchTest {

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
    void 게시물_조회_성공(){
        for(int i=1; i<=3; i++){
            Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                    .teamId(team.getId())
                    .title("제목" + i)
                    .content("내용" + i)
                    .recruitmentStatus(RecruitmentStatus.OPEN)
                    .openedAt(LocalDateTime.now())
                    .closedAt(null)
                    .build());
            if(i==2) recruitmentService.closeRecruitmentPost(leader.getId(), recruitment.getId());
        }
        for(int i=1; i<=3; i++){
            Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                    .teamId(team.getId())
                    .title("타이틀" + i)
                    .content("내용" + i)
                    .recruitmentStatus(RecruitmentStatus.OPEN)
                    .openedAt(LocalDateTime.now())
                    .closedAt(null)
                    .build());
            if(i==3) recruitmentService.closeRecruitmentPost(leader.getId(), recruitment.getId());
        }

        // 키워드 = null, 상태 = null
        // 키워드 = 타이틀, 상태 = null
        // 키워드 = 제목, 상태 = null
        // 키워드 = 1, 상태 = null
        // 키워드 = 타이틀, 상태 = OPEN
        // 키워드 = 제목, 상태 = CLOSE
        // 키워드 = 3, 상태 = null
        // 키워드 = 3, 상태 = OPEN
        // 키워드 = null, 상태 = OPEN
        // 키워드 = null, 상태 = CLOSE

        List<RecruitmentSearchResultDto> result1 = recruitmentService.getRecruitmentSearchResult(null, null);
        List<RecruitmentSearchResultDto> result2 = recruitmentService.getRecruitmentSearchResult("타이틀", null);
        List<RecruitmentSearchResultDto> result3 = recruitmentService.getRecruitmentSearchResult("제목", null);
        List<RecruitmentSearchResultDto> result4 = recruitmentService.getRecruitmentSearchResult("1", null);
        List<RecruitmentSearchResultDto> result5 = recruitmentService.getRecruitmentSearchResult("타이틀", RecruitmentStatusVisible.OPEN);
        List<RecruitmentSearchResultDto> result6 = recruitmentService.getRecruitmentSearchResult("제목", RecruitmentStatusVisible.CLOSE);
        List<RecruitmentSearchResultDto> result7 = recruitmentService.getRecruitmentSearchResult("3", null);
        List<RecruitmentSearchResultDto> result8 = recruitmentService.getRecruitmentSearchResult("3", RecruitmentStatusVisible.OPEN);
        List<RecruitmentSearchResultDto> result9 = recruitmentService.getRecruitmentSearchResult(null, RecruitmentStatusVisible.OPEN);
        List<RecruitmentSearchResultDto> result10 = recruitmentService.getRecruitmentSearchResult(null, RecruitmentStatusVisible.CLOSE);

        assertThat(result1.size()).isEqualTo(6);
        assertThat(result2.size()).isEqualTo(3);
        assertThat(result3.size()).isEqualTo(3);
        assertThat(result4.size()).isEqualTo(2);
        assertThat(result5.size()).isEqualTo(2);
        assertThat(result6.size()).isEqualTo(1);
        assertThat(result7.size()).isEqualTo(2);
        assertThat(result8.size()).isEqualTo(1);
        assertThat(result9.size()).isEqualTo(4);
        assertThat(result10.size()).isEqualTo(2);
    }

    @Test
    void 게시물_리오픈_실패_게시글존재X(){
        Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목")
                .content("테스트 내용")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> recruitmentService.reopenRecruitmentPost(leader.getId(), 100L));

        assertThat(ex.getMessage()).isEqualTo("존재하지 않는 게시글입니다.");

    }

    @Test
    void 게시물_리오픈_실패_게시물_이미_삭제(){
        Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목")
                .content("테스트 내용")
                .recruitmentStatus(RecruitmentStatus.DELETED)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> recruitmentService.reopenRecruitmentPost(leader.getId(), recruitment.getId()));

        assertThat(ex.getMessage()).isEqualTo("존재하지 않는 게시글입니다.");

    }

    @Test
    void 게시물_리오픈_실패_리더X(){
        Recruitment recruitment = recruitmentRepository.save(Recruitment.builder()
                .teamId(team.getId())
                .title("테스트 제목")
                .content("테스트 내용")
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build());

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class,
                () -> recruitmentService.reopenRecruitmentPost(member.getId(), recruitment.getId()));
        assertThat(forbiddenException.getMessage()).isEqualTo("해당 작업을 수행할 권한이 없습니다.");

    }

    @Test
    void 게시물_상세_조회(){
        RecruitmentCreateRequest recruitmentCreateRequest = RecruitmentCreateRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        recruitmentService.createRecruitmentPost(leader.getId(), team.getId(), recruitmentCreateRequest);

        Recruitment recruitment = recruitmentRepository.findAll().get(0);
        RecruitmentDetailDto recruitmentDetail = recruitmentService.getRecruitmentDetail(recruitment.getId());
        System.out.println(recruitmentDetail);
    }

}
