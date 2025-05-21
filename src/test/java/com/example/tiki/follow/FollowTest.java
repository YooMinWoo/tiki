package com.example.tiki.follow;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.follow.dto.FollowerSummaryDto;
import com.example.tiki.follow.dto.FollowingSummaryDto;
import com.example.tiki.follow.service.FollowService;
import com.example.tiki.notifircation.dto.NotificationDto;
import com.example.tiki.notifircation.service.NotificationService;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class FollowTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamUserRepository teamUserRepository;


    @Autowired
    NotificationService notificationService;

    private User leader;
    private User member;
    private Team team;
    private TeamUser teamUser;

    @BeforeEach
    void beforeEach(){
        // 유저, 팀
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

        team = teamRepository.save(Team.builder()
                .teamName("푸에르 FC")
                .teamDescription("푸에르 FC입니다.")
                .teamStatus(TeamStatus.OPEN)
                .build());

        teamUser = teamUserRepository.save(TeamUser.builder()
                .userId(leader.getId())
                .teamId(team.getId())
                .teamUserRole(TeamUserRole.ROLE_LEADER)
                .build());
    }

    @Test
    void 팔로우(){
        followService.follow(member, team.getId());

        List<NotificationDto> notificationDtoList = notificationService.getNotification(leader.getId());
        List<FollowerSummaryDto> followerList = followService.getFollowerList(team.getId());

        assertEquals(member.getId(), followerList.get(0).getUserId());
        assertEquals(member.getId(), notificationDtoList.get(0).getTargetId());

        System.out.println(notificationDtoList.get(0));
    }

    @Test
    void 언팔로우(){
        // when
        followService.follow(member, team.getId());
        followService.follow(member, team.getId());

        // then
        List<FollowerSummaryDto> followerList = followService.getFollowerList(team.getId());
        assertTrue(followerList.isEmpty());
    }

//    @Test
//    void getFollow(){
//        Long teamId = 1L;
//        for(Long i = 1L; i <= 5; i++){
//            followService.follow(i, teamId);
//        }
//
//        List<FollowerSummaryDto> followerList = followService.getFollowerList(teamId);
//        for(FollowerSummaryDto dto : followerList){
//            System.out.println(dto);
//        }
//        assertEquals(5, followerList.size());
//    }

    @Test
    void getFollowing(){
        Long userId = 1L;
        for(Long i = 1L; i <= 5; i++){
            followService.follow(member, i);
        }

        List<FollowingSummaryDto> followingList = followService.getFollowingList(userId);
        for(FollowingSummaryDto dto : followingList){
            System.out.println(dto);
        }
        assertEquals(5, followingList.size());
    }
}
