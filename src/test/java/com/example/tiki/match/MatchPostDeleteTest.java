package com.example.tiki.match;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.enums.MatchStatus;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MatchPostDeleteTest {

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
    void 매칭글_삭제(){
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
                .matchStatus(MatchStatus.CANCELED)
                .build();
        MatchPost post3 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 333")
                .startTime(LocalDateTime.of(2025,5,27,14, 0))
                .endTime(LocalDateTime.of(2025,5,27,16, 0))
                .region("인천")
                .matchStatus(MatchStatus.UNMATCHED)
                .build();
        MatchPost post4 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 444")
                .startTime(LocalDateTime.of(2025,5,27,16, 0))
                .endTime(LocalDateTime.of(2025,5,27,18, 0))
                .region("인천")
                .matchStatus(MatchStatus.COMPLETED)
                .build();
        MatchPost post5 = MatchPost.builder()
                .hostTeamId(team.getId())
                .title("매칭 모집합니다 111")
                .startTime(LocalDateTime.of(2025,5,28,10, 0))
                .endTime(LocalDateTime.of(2025,5,28,12, 0))
                .region("서울")
                .matchStatus(MatchStatus.OPEN)
                .build();
        matchPostRepository.save(post1);
        matchPostRepository.save(post2);
        matchPostRepository.save(post3);
        matchPostRepository.save(post4);
        matchPostRepository.save(post5);

        matchPostService.deleteMatchPost(leader.getId(), post1.getId());
        assertThrows(IllegalStateException.class, () -> matchPostService.deleteMatchPost(leader.getId(), post2.getId()));
        assertThrows(IllegalStateException.class, () -> matchPostService.deleteMatchPost(leader.getId(), post3.getId()));
        assertThrows(IllegalStateException.class, () -> matchPostService.deleteMatchPost(leader.getId(), post4.getId()));
        assertThrows(NotFoundException.class, () -> matchPostService.deleteMatchPost(leader.getId(), 100L));
        assertThrows(ForbiddenException.class, () -> matchPostService.deleteMatchPost(member.getId(), post5.getId()));
        assertThat(post1.getMatchStatus()).isEqualTo(MatchStatus.DELETED);
    }
}
