package com.example.tiki.match;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.dto.*;
import com.example.tiki.match.repository.MatchPostRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private MatchPostService matchPostService;

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
                .startTime(LocalDateTime.of(2025,5,27,10, 0))
                .endTime(LocalDateTime.of(2025,5,27,12, 0))
                .region("서울")
                .matchStatus(MatchStatus.OPEN)
                .build();
        MatchPost post2 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 222")
                .startTime(LocalDateTime.of(2025,5,27,12, 0))
                .endTime(LocalDateTime.of(2025,5,27,14, 0))
                .region("인천")
                .matchStatus(MatchStatus.OPEN)
                .build();
        MatchPost post3 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 333")
                .startTime(LocalDateTime.of(2025,5,27,14, 0))
                .endTime(LocalDateTime.of(2025,5,27,16, 0))
                .region("인천")
                .matchStatus(MatchStatus.OPEN)
                .build();
        MatchPost post4 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 444")
                .startTime(LocalDateTime.of(2025,5,27,16, 0))
                .endTime(LocalDateTime.of(2025,5,27,18, 0))
                .region("인천")
                .matchStatus(MatchStatus.MATCHED)
                .build();
        matchPostRepository.save(post1);
        matchPostRepository.save(post2);
        matchPostRepository.save(post3);
        matchPostRepository.save(post4);

        MatchPostSearchCondition condition1 = MatchPostSearchCondition.builder()
                .keyword("")
                .matchDate(LocalDate.of(2025,5,27))
                .region("")
                .status(MatchPostStatusVisible.OPEN)
                .build();

        MatchPostSearchCondition condition2 = MatchPostSearchCondition.builder()
                .keyword("2")
                .matchDate(LocalDate.of(2025,5,27))
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
                .matchDate(LocalDate.of(2025,5,27))
                .region("인천")
//                .status(MatchPostStatusVisible.OPEN)
                .build();

        List<MatchPostSearchResponse> result1 = matchPostService.searchMatchPost(condition1);
        List<MatchPostSearchResponse> result2 = matchPostService.searchMatchPost(condition2);
        List<MatchPostSearchResponse> result3 = matchPostService.searchMatchPost(condition3);
        List<MatchPostSearchResponse> result4 = matchPostService.searchMatchPost(condition4);
        assertThat(result1.size()).isEqualTo(3);
        assertThat(result2.size()).isEqualTo(1);
        assertThat(result3.size()).isEqualTo(0);
        assertThat(result4.size()).isEqualTo(3);
    }

    @Test
    void 매칭글_팀별_조회_성공(){
        Team team2 = teamRepository.save(
                Team.builder()
                        .teamName("테스트 팀")
                        .teamDescription("테스트 설명")
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
        MatchPost post2 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 222")
                .startTime(LocalDateTime.of(2025,5,27,12, 0))
                .endTime(LocalDateTime.of(2025,5,27,14, 0))
                .region("인천")
                .matchStatus(MatchStatus.OPEN)
                .build();
        MatchPost post3 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 333")
                .startTime(LocalDateTime.of(2025,5,27,14, 0))
                .endTime(LocalDateTime.of(2025,5,27,16, 0))
                .region("인천")
                .matchStatus(MatchStatus.OPEN)
                .build();
        MatchPost post4 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 444")
                .startTime(LocalDateTime.of(2025,5,27,16, 0))
                .endTime(LocalDateTime.of(2025,5,27,18, 0))
                .region("인천")
                .matchStatus(MatchStatus.MATCHED)
                .build();

        MatchPost post5 = MatchPost.builder()
                .hostTeamId(team2.getId())
                .title("매칭 모집합니다 111")
                .startTime(LocalDateTime.of(2025,5,27,10, 0))
                .endTime(LocalDateTime.of(2025,5,27,12, 0))
                .region("서울")
                .matchStatus(MatchStatus.OPEN)
                .build();
        MatchPost post6 = MatchPost.builder()
                .hostTeamId(team2.getId())
                .title("매칭 모집합니다 222")
                .startTime(LocalDateTime.of(2025,5,27,12, 0))
                .endTime(LocalDateTime.of(2025,5,27,14, 0))
                .region("인천")
                .matchStatus(MatchStatus.OPEN)
                .build();
        MatchPost post7 = MatchPost.builder()
                .hostTeamId(team2.getId())
                .title("매칭 모집합니다 333")
                .startTime(LocalDateTime.of(2025,5,27,14, 0))
                .endTime(LocalDateTime.of(2025,5,27,16, 0))
                .region("인천")
                .matchStatus(MatchStatus.MATCHED)
                .build();

        matchPostRepository.save(post1);
        matchPostRepository.save(post2);
        matchPostRepository.save(post3);
        matchPostRepository.save(post4);
        matchPostRepository.save(post5);
        matchPostRepository.save(post6);
        matchPostRepository.save(post7);

        MatchPostByTeamSearchCondition condition1 = MatchPostByTeamSearchCondition.builder()
                .keyword("")
                .matchDate(LocalDate.of(2025,5,27))
                .region("")
                .status(MatchPostByTeamStatusVisible.OPEN)
                .build();

        MatchPostByTeamSearchCondition condition2 = MatchPostByTeamSearchCondition.builder()
                .keyword("2")
                .matchDate(LocalDate.of(2025,5,27))
                .region("")
                .status(MatchPostByTeamStatusVisible.OPEN)
                .build();

        MatchPostByTeamSearchCondition condition3 = MatchPostByTeamSearchCondition.builder()
//                .keyword("2")
//                .matchDate(LocalDate.now())
//                .region("")
                .status(MatchPostByTeamStatusVisible.MATCHED)
                .build();

        MatchPostByTeamSearchCondition condition4 = MatchPostByTeamSearchCondition.builder()
//                .keyword("2")
                .matchDate(LocalDate.of(2025,5,27))
                .region("인천")
//                .status(MatchPostStatusVisible.OPEN)
                .build();

        List<MatchPostSearchResponse> result1 = matchPostService.searchMatchPostByTeam(team.getId(), condition1);
        List<MatchPostSearchResponse> result2 = matchPostService.searchMatchPostByTeam(team.getId(), condition2);
        List<MatchPostSearchResponse> result3 = matchPostService.searchMatchPostByTeam(team.getId(), condition3);
        List<MatchPostSearchResponse> result4 = matchPostService.searchMatchPostByTeam(team.getId(), condition4);
        List<MatchPostSearchResponse> result5 = matchPostService.searchMatchPostByTeam(team2.getId(), condition1);
        List<MatchPostSearchResponse> result6 = matchPostService.searchMatchPostByTeam(team2.getId(), condition2);
        List<MatchPostSearchResponse> result7 = matchPostService.searchMatchPostByTeam(team2.getId(), condition3);
        assertThat(result1.size()).isEqualTo(3);
        assertThat(result2.size()).isEqualTo(1);
        assertThat(result3.size()).isEqualTo(1);
        assertThat(result4.size()).isEqualTo(3);
        assertThat(result5.size()).isEqualTo(2);
        assertThat(result6.size()).isEqualTo(1);
        assertThat(result7.size()).isEqualTo(1);
    }

    @Test
    void 매칭글_상세_조회_상대X(){
        MatchPostRequest request = MatchPostRequest.builder()
                .hostTeamId(team.getId())
                .title("매칭글 테스트 제목")
                .content("매칭글 테스트 내용")
                .startTime(LocalDateTime.of(2025,5,27,10, 0))
                .endTime(LocalDateTime.of(2025,5,27,12, 0))
                .region("인천광역시")
                .city("미추홀구")
                .roadName("용오로")
                .buildingNumber("82")
                .detailAddress("동아아파트 4동 608호")
                .build();

        matchPostService.createMatchPost(leader.getId(), request);

        MatchPost matchPost = matchPostRepository.findAll().get(0);

        MatchPostResponse matchPostDetail = matchPostService.getMatchPostDetail(matchPost.getId());
        System.out.println(matchPostDetail);
    }

}
