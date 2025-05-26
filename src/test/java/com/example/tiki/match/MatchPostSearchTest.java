package com.example.tiki.match;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.dto.MatchPostRequest;
import com.example.tiki.match.dto.MatchPostSearchCondition;
import com.example.tiki.match.dto.MatchPostSearchResponse;
import com.example.tiki.match.dto.MatchPostStatusVisible;
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
public class MatchPostSearchTest {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamUserRepository teamUserRepository;

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

    }

    @Test
    void 매칭글_조회_성공(){
        MatchPost post1 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 111")
                .matchDate(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .region("서울")
                .matchStatus(MatchStatus.OPEN)
                .build();
        MatchPost post2 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 222")
                .matchDate(LocalDate.now())
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(14, 0))
                .region("인천")
                .matchStatus(MatchStatus.OPEN)
                .build();
        MatchPost post3 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 333")
                .matchDate(LocalDate.now())
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(16, 0))
                .region("인천")
                .matchStatus(MatchStatus.OPEN)
                .build();
        MatchPost post4 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 444")
                .matchDate(LocalDate.now())
                .startTime(LocalTime.of(16, 0))
                .endTime(LocalTime.of(18, 0))
                .region("인천")
                .matchStatus(MatchStatus.MATCHED)
                .build();
        matchPostRepository.save(post1);
        matchPostRepository.save(post2);
        matchPostRepository.save(post3);
        matchPostRepository.save(post4);

        MatchPostSearchCondition condition1 = MatchPostSearchCondition.builder()
                .keyword("")
                .matchDate(LocalDate.now())
                .region("")
                .status(MatchPostStatusVisible.OPEN)
                .build();

        MatchPostSearchCondition condition2 = MatchPostSearchCondition.builder()
                .keyword("2")
                .matchDate(LocalDate.now())
                .region("")
                .status(MatchPostStatusVisible.OPEN)
                .build();

        MatchPostSearchCondition condition3 = MatchPostSearchCondition.builder()
//                .keyword("2")
//                .matchDate(LocalDate.now())
//                .region("")
                .status(MatchPostStatusVisible.MATCHED)
                .build();

        MatchPostSearchCondition condition4 = MatchPostSearchCondition.builder()
//                .keyword("2")
//                .matchDate(LocalDate.now())
                .region("인천")
//                .status(MatchPostStatusVisible.OPEN)
                .build();

        List<MatchPostSearchResponse> result1 = matchService.searchMatchPost(condition1);
        List<MatchPostSearchResponse> result2 = matchService.searchMatchPost(condition2);
        List<MatchPostSearchResponse> result3 = matchService.searchMatchPost(condition3);
        List<MatchPostSearchResponse> result4 = matchService.searchMatchPost(condition4);
        assertThat(result1.size()).isEqualTo(3);
        assertThat(result2.size()).isEqualTo(1);
        assertThat(result3.size()).isEqualTo(1);
        assertThat(result4.size()).isEqualTo(3);
    }

}
