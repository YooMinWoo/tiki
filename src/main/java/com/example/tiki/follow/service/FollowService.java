package com.example.tiki.follow.service;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.dto.FollowerSummaryDto;
import com.example.tiki.follow.dto.FollowingSummaryDto;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.team.domain.Team;
import com.example.tiki.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final TeamRepository teamRepository;
    private final AuthRepository authRepository;


    // 팔로워 조회
    public List<FollowerSummaryDto> getFollowerList(Long teamId){
        List<FollowerSummaryDto> result = new ArrayList<>();

        // 1. 팔로우 목록 조회
        List<Follow> follows = followRepository.findByTeamId(teamId);

        // 2. 팔로워 유저 ID 수집
        List<Long> userIds = new ArrayList<>();
        for (Follow follow : follows) {
            userIds.add(follow.getUserId());
        }

        // 3. 유저 ID로 User 전체 조회 (N+1 방지)
        List<User> users = authRepository.findAllById(userIds);

        // 4. ID -> User 맵핑
        Map<Long, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }

        // 5. DTO로 변환
        for (Follow follow : follows) {
            User user = userMap.get(follow.getUserId());
            if (user == null) continue;

            FollowerSummaryDto dto = FollowerSummaryDto.builder()
                    .userId(user.getId())
                    .userName(user.getName())
                    .userEmail(user.getEmail())
                    .build();

            result.add(dto);
        }

        return result;
    }

    // 팔로잉 조회
    public List<FollowingSummaryDto> getFollowingList(Long userId){
        List<FollowingSummaryDto> result = new ArrayList<>();

        // 1. 팔로잉 목록 조회
        List<Follow> follows = followRepository.findByUserId(userId);

        // 2. 팔로잉 팀 ID 수집
        List<Long> teamIds = new ArrayList<>();
        for (Follow follow : follows) {
            teamIds.add(follow.getTeamId());
        }

        // 3. 팀 ID로 Team 전체 조회 (N+1 방지)
        List<Team> teams = teamRepository.findAllById(teamIds);

        // 4. ID -> Team 맵핑
        Map<Long, Team> teamMap = new HashMap<>();
        for (Team team : teams) {
            teamMap.put(team.getId(), team);
        }

        // 5. DTO로 변환
        for (Follow follow : follows) {
            Team team = teamMap.get(follow.getTeamId());
            if (team == null) continue;

            FollowingSummaryDto dto = FollowingSummaryDto.builder()
                    .teamId(team.getId())
                    .teamName(team.getTeamName())
                    .build();

            result.add(dto);
        }

        return result;
    }

    // 팔로우/언팔로우 기능(토글)
    @Transactional
    public void follow(Long userId, Long teamId){
//        authRepository.findById(userId).orElseThrow(() -> new NotFoundException("계정 정보를 불러오는 중 에러가 발생하였습니다."));
        teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));

        followRepository.findByUserIdAndTeamId(userId, teamId).ifPresentOrElse(
                // 언팔로우
                followRepository::delete,

                // 팔로우
                () -> {
                    // 알림 기능 추가하기
                    followRepository.save(Follow.builder()
                            .userId(userId)
                            .teamId(teamId)
                            .build());
                }
        );
    }
}
