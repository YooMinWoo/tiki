package com.example.tiki.follow;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.follow.dto.FollowerSummaryDto;
import com.example.tiki.follow.dto.FollowingSummaryDto;
import com.example.tiki.follow.service.FollowService;
import com.example.tiki.team.domain.Team;
import com.example.tiki.team.domain.TeamStatus;
import com.example.tiki.team.repository.TeamRepository;
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


    @BeforeEach
    void setUp(){
        // 유저, 팀
        for(int i=1; i<=10; i++){
            authRepository.save(
                        User.builder()
                                .name("test"+i)
                                .email("test"+i+"@test.com")
                                .role(Role.ROLE_USER)
                                .build());
        }
        for(int i=1; i<=10; i++){
            teamRepository.save(Team.builder()
                    .teamName("test" + i + " FC")
                    .teamDescription("test" + i +" FC입니다.")
                    .teamStatus(TeamStatus.OPEN)
                    .build());
        }

    }

    @Test
    void UnfollowToFollow(){
        // given
        Long userId = 1L;
        Long teamId = 1L;

        // when
        followService.follow(userId, teamId);

        // then
        List<FollowerSummaryDto> followerList = followService.getFollowerList(teamId);
        assertEquals(userId, followerList.get(0).getUserId());
    }

    @Test
    void FollowToUnfollow(){
        // given
        Long userId = 1L;
        Long teamId = 1L;

        // when
        followService.follow(userId, teamId);
        followService.follow(userId, teamId);

        // then
        List<FollowerSummaryDto> followerList = followService.getFollowerList(teamId);
        assertTrue(followerList.isEmpty());
    }

    @Test
    void getFollow(){
        Long teamId = 1L;
        for(Long i = 1L; i <= 5; i++){
            followService.follow(i, teamId);
        }

        List<FollowerSummaryDto> followerList = followService.getFollowerList(teamId);
        for(FollowerSummaryDto dto : followerList){
            System.out.println(dto);
        }
        assertEquals(5, followerList.size());
    }

    @Test
    void getFollowing(){
        Long userId = 1L;
        for(Long i = 1L; i <= 5; i++){
            followService.follow(userId, i);
        }

        List<FollowingSummaryDto> followingList = followService.getFollowingList(userId);
        for(FollowingSummaryDto dto : followingList){
            System.out.println(dto);
        }
        assertEquals(5, followingList.size());
    }
}
