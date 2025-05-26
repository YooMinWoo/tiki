package com.example.tiki.match;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.dto.MatchPostRequest;
import com.example.tiki.match.dto.MatchPostSearchCondition;
import com.example.tiki.match.dto.MatchPostSearchResponse;
import com.example.tiki.match.dto.MatchPostStatusVisible;
import com.example.tiki.match.repository.MatchPostRepository;
import com.example.tiki.match.service.MatchService;
import com.example.tiki.notifircation.domain.Notification;
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
public class MatchPostUpdateTest {

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
    void 매칭글_수정_성공(){
        MatchPostRequest beforeRequest = MatchPostRequest.builder()
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

        matchService.createMatchPost(leader.getId(), beforeRequest);

        MatchPost matchPost = matchPostRepository.findAll().get(0);
        String beforeAddress = matchPost.getFullAddress();
        String beforeTitle = matchPost.getTitle();
        String beforeContent = matchPost.getContent();
        Double beforeLatitude = matchPost.getLatitude();
        Double beforeLongitude = matchPost.getLongitude();

        MatchPostRequest afterRequest = MatchPostRequest.builder()
                .hostTeamId(team.getId())
                .title("매칭글 테스트 제목 변경")
                .content("매칭글 테스트 내용 변경")
                .startTime(LocalDateTime.of(2025,5,27,10, 0))
                .endTime(LocalDateTime.of(2025,5,27,12, 0))
                .region("인천광역시")
                .city("미추홀구")
                .roadName("용오로")
                .buildingNumber("82")
                .detailAddress("동아아파트 4동 608호")
                .build();

        matchService.updateMatchPost(leader.getId(), matchPost.getId(), afterRequest);

        String afterAddress = matchPost.getFullAddress();
        String afterTitle = matchPost.getTitle();
        String afterContent = matchPost.getContent();
        Double afterLatitude = matchPost.getLatitude();
        Double afterLongitude = matchPost.getLongitude();

        assertThat(beforeAddress).isNotEqualTo(afterAddress);
        assertThat(beforeTitle).isNotEqualTo(afterTitle);
        assertThat(beforeContent).isNotEqualTo(afterContent);
        assertThat(beforeLatitude).isNotEqualTo(afterLatitude);
        assertThat(beforeLongitude).isNotEqualTo(afterLongitude);
    }

    @Test
    void 매칭글_수정_실패(){
        MatchPostRequest beforeRequest = MatchPostRequest.builder()
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

        matchService.createMatchPost(leader.getId(), beforeRequest);

        MatchPost matchPost = matchPostRepository.findAll().get(0);

        MatchPostRequest afterRequest = MatchPostRequest.builder()
                .hostTeamId(team.getId())
                .title("매칭글 테스트 제목 변경")
                .content("매칭글 테스트 내용 변경")
                .startTime(LocalDateTime.of(2025,5,27,10, 0))
                .endTime(LocalDateTime.of(2025,5,27,12, 0))
                .region("인천광역시")
                .city("미추홀구")
                .roadName("용오로")
                .buildingNumber("82")
                .detailAddress("동아아파트 4동 608호")
                .build();

        assertThrows(NotFoundException.class, () -> matchService.updateMatchPost(leader.getId(), 100L, afterRequest));
        afterRequest.setHostTeamId(100L);
        assertThrows(ForbiddenException.class, () -> matchService.updateMatchPost(leader.getId(), matchPost.getId(), afterRequest));
        afterRequest.setHostTeamId(team.getId());
        matchPost.changeStatus(MatchStatus.MATCHED);
        assertThrows(IllegalStateException.class, () -> matchService.updateMatchPost(leader.getId(), matchPost.getId(), afterRequest));
    }

}
