package com.example.tiki.follow.controller;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.follow.dto.FollowerSummaryDto;
import com.example.tiki.follow.dto.FollowingSummaryDto;
import com.example.tiki.follow.service.FollowService;
import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.team.dto.TeamCreateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Follow Controller", description = "팔로우 관련 기능")
public class FollowController {

    private final FollowService followService;

    @GetMapping("/users/{userId}/followings")
    @Operation(summary = "특정 회원의 팔로우한 팀 목록 조회", description = "특정 유저가 팔로우 중인 팀 목록 반환 API")
    public ResponseEntity<?> getMyFollowings(@PathVariable("userId") Long userId) {
        List<FollowingSummaryDto> followings = followService.getFollowingList(userId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팔로잉 목록 조회 성공", followings));
    }

    @GetMapping("/teams/{teamId}/followers")
    @Operation(summary = "팀의 팔로워 목록 조회", description = "특정 팀을 팔로우 중인 사용자 목록 반환 API")
    public ResponseEntity<?> getTeamFollowers(@PathVariable("teamId") Long teamId) {
        List<FollowerSummaryDto> followers = followService.getFollowerList(teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팔로워 목록 조회 성공", followers));
    }

    @PostMapping("/teams/{teamId}/follow")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팔로우/언팔로우 기능",
            description = "팔로우/언팔로우 API"
    )
    public ResponseEntity<?> follow(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    @PathVariable("teamId") Long teamId){
        User user = customUserDetails.getUser();
        followService.follow(user, teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팔로우/언팔로우 (토글) ok", null));
    }
}
