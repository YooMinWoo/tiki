package com.example.tiki.follow.service;

import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final TeamRepository teamRepository;
    private final AuthRepository authRepository;

    public void follow(Long userId, Long teamId){
        authRepository.findById(userId).orElseThrow(() -> new NotFoundException("계정 정보를 불러오는 중 에러가 발생하였습니다."));
        teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));

        followRepository.findByUserIdAndTeamId(userId, teamId).ifPresentOrElse(
                followRepository::delete,
                () -> {
                    followRepository.save(Follow.builder()
                            .userId(userId)
                            .teamId(teamId)
                            .build());
                }
        );
    }
}
