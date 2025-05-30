package com.example.tiki.match;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.entity.MatchRequest;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.domain.enums.RequestStatus;
import com.example.tiki.match.dto.DecideStatus;
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
import java.util.List;

import static com.example.tiki.match.domain.entity.QMatchRequest.matchRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MatchRequestDecideTest {

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
    private User member2;
    private User member3;
    private User member4;
    private Team team;
    private Team team2;
    private Team team5;
    private Team team6;
    private Team team7;

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
        member2 = authRepository.save(
                User.builder()
                        .name("member")
                        .email("member@naver.com")
                        .role(Role.ROLE_USER)
                        .build());
        member3 = authRepository.save(
                User.builder()
                        .name("member")
                        .email("member@naver.com")
                        .role(Role.ROLE_USER)
                        .build());
        member4 = authRepository.save(
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

        team5 = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀555")
                        .teamDescription("테스트 설명555")
                        .teamStatus(TeamStatus.ACTIVE)
                        .build());
        team6 = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀666")
                        .teamDescription("테스트 설명666")
                        .teamStatus(TeamStatus.ACTIVE)
                        .build());
        team7 = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀777")
                        .teamDescription("테스트 설명777")
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

        teamUserRepository.save(TeamUser.builder()
                .userId(member2.getId())
                .teamId(team5.getId())
                .teamUserRole(TeamUserRole.ROLE_LEADER)
                .teamUserStatus(TeamUserStatus.APPROVED)
                .build());

        teamUserRepository.save(TeamUser.builder()
                .userId(member3.getId())
                .teamId(team6.getId())
                .teamUserRole(TeamUserRole.ROLE_LEADER)
                .teamUserStatus(TeamUserStatus.APPROVED)
                .build());

        teamUserRepository.save(TeamUser.builder()
                .userId(member4.getId())
                .teamId(team7.getId())
                .teamUserRole(TeamUserRole.ROLE_LEADER)
                .teamUserStatus(TeamUserStatus.APPROVED)
                .build());

    }

    @Test
    void 매칭_수락_성공(){
        MatchPost matchPost = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 111")
                .startTime(LocalDateTime.of(2025,5,27,10, 0))
                .endTime(LocalDateTime.of(2025,5,27,12, 0))
                .region("서울")
                .matchStatus(MatchStatus.OPEN)
                .build();

        matchPostRepository.save(matchPost);

        // 매칭 신청
        matchRequestService.applyForMatch(member.getId(), team2.getId(), matchPost.getId());
        matchRequestService.applyForMatch(member2.getId(), team5.getId(), matchPost.getId());
        matchRequestService.applyForMatch(member3.getId(), team6.getId(), matchPost.getId());
        matchRequestService.applyForMatch(member4.getId(), team7.getId(), matchPost.getId());

        MatchRequest matchRequest = matchRequestRepository.findByMatchPostIdAndApplicantTeamId(matchPost.getId(), team2.getId()).get();

        // 매칭 수락
        matchRequestService.decideMatchRequest(leader.getId(), matchRequest.getId(), DecideStatus.ACCEPTED);

        assertThat(matchPost.getMatchStatus()).isEqualTo(MatchStatus.MATCHED);
        assertThat(matchPost.getApplicantTeamId()).isEqualTo(team2.getId());

        List<MatchRequest> matchRequestList = matchRequestRepository.findByMatchPostId(matchPost.getId());
        for (MatchRequest request : matchRequestList) {
            System.out.println(request);
        }

        List<Notification> notificationList = notificationRepository.findAll();
        for (Notification notification : notificationList) {
            System.out.println(notification);
        }
    }

    @Test
    void 매칭_거절_성공(){
        MatchPost matchPost = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 111")
                .startTime(LocalDateTime.of(2025,5,27,10, 0))
                .endTime(LocalDateTime.of(2025,5,27,12, 0))
                .region("서울")
                .matchStatus(MatchStatus.OPEN)
                .build();

        matchPostRepository.save(matchPost);

        // 매칭 신청
        matchRequestService.applyForMatch(member.getId(), team2.getId(), matchPost.getId());

        // 매칭 조회
        MatchRequest matchRequest = matchRequestRepository.findAll().get(0);

        // 매칭 거절
        matchRequestService.decideMatchRequest(leader.getId(), matchRequest.getId(), DecideStatus.REJECTED);

        assertThat(matchPost.getMatchStatus()).isEqualTo(MatchStatus.OPEN);
        assertThat(matchPost.getApplicantTeamId()).isNull();

        assertThat(matchRequest.getMatchPostId()).isEqualTo(matchPost.getId());
        assertThat(matchRequest.getApplicantTeamId()).isEqualTo(team2.getId());
        assertThat(matchRequest.getRequestStatus()).isEqualTo(RequestStatus.REJECTED);

        Notification notification = notificationRepository.findByUserId(member.getId()).get(0);

        assertThat(notification.getUserId()).isEqualTo(member.getId());
        assertThat(notification.getTargetId()).isEqualTo(matchPost.getId());
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.MATCHPOST);

        System.out.println(notification.getMessage());
    }

}
