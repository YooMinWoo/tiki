package com.example.tiki.match;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.entity.MatchRequest;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.domain.enums.RequestStatus;
import com.example.tiki.match.dto.*;
import com.example.tiki.match.repository.MatchPostRepository;
import com.example.tiki.match.repository.MatchRequestRepository;
import com.example.tiki.match.service.MatchPostService;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class MatchRequestListAboutMatchPostTest {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamUserRepository teamUserRepository;

    @Autowired
    private MatchPostService matchPostService;

    @Autowired
    private MatchPostRepository matchPostRepository;

    @Autowired
    private MatchRequestRepository matchRequestRepository;

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
    void 매칭글에_대한_요청_목록_조회_성공(){
        Team team1 = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀111")
                        .teamDescription("테스트 설명111")
                        .teamStatus(TeamStatus.ACTIVE)
                        .build());
        Team team2 = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀222")
                        .teamDescription("테스트 설명2222")
                        .teamStatus(TeamStatus.ACTIVE)
                        .build());
        Team team3 = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀333")
                        .teamDescription("테스트 설명333")
                        .teamStatus(TeamStatus.ACTIVE)
                        .build());
        MatchPost post1 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 111")
                .startTime(LocalDateTime.of(2025,5,27,10, 0))
                .endTime(LocalDateTime.of(2025,5,27,12, 0))
                .region("서울")
                .matchStatus(MatchStatus.OPEN)
                .build();
        matchPostRepository.save(post1);

        matchRequestRepository.save(
                MatchRequest.builder()
                        .matchPostId(post1.getId())
                        .applicantTeamId(team1.getId())
                        .requestStatus(RequestStatus.PENDING)
                        .build()
        );
        matchRequestRepository.save(
                MatchRequest.builder()
                        .matchPostId(post1.getId())
                        .applicantTeamId(team2.getId())
                        .requestStatus(RequestStatus.PENDING)
                        .build()
        );
        matchRequestRepository.save(
                MatchRequest.builder()
                        .matchPostId(post1.getId())
                        .applicantTeamId(team3.getId())
                        .requestStatus(RequestStatus.PENDING)
                        .build()
        );

        List<MatchRequestsForPost> matchRequestsForPost = matchPostService.getMatchRequestsForPost(post1.getId());
        for (MatchRequestsForPost matchRequestForPost : matchRequestsForPost) {
            System.out.println(matchRequestForPost);
        }

    }

    @Test
    void test(){
        Team team1 = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀111")
                        .teamDescription("테스트 설명111")
                        .teamStatus(TeamStatus.ACTIVE)
                        .build());
        Team team2 = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀222")
                        .teamDescription("테스트 설명2222")
                        .teamStatus(TeamStatus.ACTIVE)
                        .build());
        Team team3 = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀333")
                        .teamDescription("테스트 설명333")
                        .teamStatus(TeamStatus.ACTIVE)
                        .build());
        MatchPost post1 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 111")
                .startTime(LocalDateTime.of(2025,5,27,10, 0))
                .endTime(LocalDateTime.of(2025,5,27,12, 0))
                .region("서울")
                .matchStatus(MatchStatus.OPEN)
                .build();
        matchPostRepository.save(post1);

        MatchRequest matchRequest = matchRequestRepository.save(
                MatchRequest.builder()
                        .matchPostId(post1.getId())
                        .applicantTeamId(team1.getId())
                        .requestStatus(RequestStatus.PENDING)
                        .build()
        );
        matchRequestRepository.save(
                MatchRequest.builder()
                        .matchPostId(post1.getId())
                        .applicantTeamId(team2.getId())
                        .requestStatus(RequestStatus.PENDING)
                        .build()
        );
        matchRequestRepository.save(
                MatchRequest.builder()
                        .matchPostId(post1.getId())
                        .applicantTeamId(team3.getId())
                        .requestStatus(RequestStatus.PENDING)
                        .build()
        );

        matchRequest.changeStatus(RequestStatus.ACCEPTED);
        List<MatchRequest> matchRequestList = matchRequestRepository.findAllByMatchPostIdAndRequestStatus(post1.getId(), RequestStatus.PENDING);
        for (MatchRequest request : matchRequestList) {
            System.out.println(request);
        }
    }


}
