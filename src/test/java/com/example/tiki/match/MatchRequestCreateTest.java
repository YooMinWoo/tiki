package com.example.tiki.match;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.entity.MatchRequest;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.domain.enums.RequestStatus;
import com.example.tiki.match.dto.MatchPostRequest;
import com.example.tiki.match.repository.MatchPostRepository;
import com.example.tiki.match.repository.MatchRequestRepository;
import com.example.tiki.match.service.MatchPostService;
import com.example.tiki.match.service.MatchRequestService;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MatchRequestCreateTest {

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
    private NotificationRepository notificationRepository;

    @Autowired
    private MatchPostService matchPostService;

    @Autowired
    private MatchPostRepository matchPostRepository;

    @Autowired
    private MatchRequestRepository matchRequestRepository;

    @Autowired
    private MatchRequestService matchRequestService;

    private User leader;
    private User member;
    private Team team;
    private Team team2;

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

        team2 = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀2")
                        .teamDescription("테스트 설명2")
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
                .teamId(team2.getId())
                .teamUserRole(TeamUserRole.ROLE_LEADER)
                .teamUserStatus(TeamUserStatus.APPROVED)
                .build());

    }

    @Test
    void 매칭요청_성공(){
        MatchPost matchPost = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 111")
                .startTime(LocalDateTime.of(2025,5,27,10, 0))
                .endTime(LocalDateTime.of(2025,5,27,12, 0))
                .region("서울")
                .matchStatus(MatchStatus.OPEN)
                .build();

        matchPostRepository.save(matchPost);

        matchRequestService.applyForMatch(member.getId(), team2.getId(), matchPost.getId());

        Notification notification = notificationRepository.findByUserId(leader.getId()).get(0);
        MatchRequest matchRequest = matchRequestRepository.findAll().get(0);

        assertThat(matchPost.getMatchStatus()).isEqualTo(MatchStatus.OPEN);

        assertThat(matchRequest.getMatchPostId()).isEqualTo(matchPost.getId());
        assertThat(matchRequest.getApplicantTeamId()).isEqualTo(team2.getId());
        assertThat(matchRequest.getRequestStatus()).isEqualTo(RequestStatus.PENDING);

        assertThat(notification.getTargetId()).isEqualTo(team2.getId());
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.MATCHREQUEST);
        assertThat(notification.getUserId()).isEqualTo(leader.getId());

        System.out.println(notification.getMessage());
    }

    @Test
    void 매칭글_생성_실패_시작시간_마감시간_오기재(){
        MatchPostRequest request = MatchPostRequest.builder()
                .hostTeamId(team.getId())
                .title("매칭글 테스트 제목")
                .content("매칭글 테스트 내용")
                .startTime(LocalDateTime.of(2025,5,27,14, 0))
                .endTime(LocalDateTime.of(2025,5,27,12, 0))
                .region("인천광역시")
                .city("미추홀구")
                .roadName("용오로")
                .buildingNumber("82")
                .detailAddress("동아아파트 4동 608호")
                .build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> matchPostService.createMatchPost(leader.getId(), request));
        assertThat(ex.getMessage()).isEqualTo("종료시간은 시작시간과 같거나 빠를 수 없습니다.");
    }
}
