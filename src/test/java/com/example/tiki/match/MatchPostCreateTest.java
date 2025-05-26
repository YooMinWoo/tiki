package com.example.tiki.match;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.dto.MatchPostRequest;
import com.example.tiki.match.repository.MatchPostRepository;
import com.example.tiki.match.service.MatchService;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MatchPostCreateTest {

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

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchPostRepository matchPostRepository;

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

    @Test
    void 매칭글_생성_성공(){
        MatchPostRequest request = MatchPostRequest.builder()
                .hostTeamId(team.getId())
                .title("매칭글 테스트 제목")
                .content("매칭글 테스트 내용")
                .matchDate(LocalDate.parse("2025-05-17"))
                .startTime(LocalTime.parse("10:00"))
                .endTime(LocalTime.parse("12:00"))
                .region("인천광역시")
                .city("미추홀구")
                .roadName("용오로")
                .buildingNumber("82")
                .detailAddress("동아아파트 4동 608호")
                .build();

        matchService.createMatchPost(leader.getId(), request);

        MatchPost matchPost = matchPostRepository.findAll().get(0);
        Notification notification = notificationRepository.findAll().get(0);

        assertThat(matchPost.getMatchStatus()).isEqualTo(MatchStatus.OPEN);
        assertThat(matchPost.getTitle()).isEqualTo(request.getTitle());
        assertThat(matchPost.getContent()).isEqualTo(request.getContent());
        assertThat(matchPost.getLatitude()).isEqualTo(37.4528915781653);
        assertThat(matchPost.getLongitude()).isEqualTo(126.644416664576);

        assertThat(notification.getTargetId()).isEqualTo(matchPost.getId());
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.MATCHPOST);
        assertThat(notification.getUserId()).isEqualTo(member.getId());

        System.out.println(matchPost.getMatchStatus());
    }

    @Test
    void 매칭글_생성_실패_시작시간_마감시간_오기재(){
        MatchPostRequest request = MatchPostRequest.builder()
                .hostTeamId(team.getId())
                .title("매칭글 테스트 제목")
                .content("매칭글 테스트 내용")
                .matchDate(LocalDate.parse("2025-05-17"))
                .startTime(LocalTime.parse("14:00"))
                .endTime(LocalTime.parse("12:00"))
                .region("인천광역시")
                .city("미추홀구")
                .roadName("용오로")
                .buildingNumber("82")
                .detailAddress("동아아파트 4동 608호")
                .build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> matchService.createMatchPost(leader.getId(), request));
        assertThat(ex.getMessage()).isEqualTo("종료시간은 시작시간과 같거나 빠를 수 없습니다.");
    }
}
