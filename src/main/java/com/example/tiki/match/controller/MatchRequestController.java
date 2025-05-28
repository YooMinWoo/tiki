package com.example.tiki.match.controller;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.match.dto.DecideStatus;
import com.example.tiki.match.dto.MatchRequestResponse;
import com.example.tiki.match.service.MatchRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/match-requests")
@Tag(name = "MatchRequest Controller", description = "매칭 신청 관련 API")
public class MatchRequestController {
    private final MatchRequestService matchRequestService;

    /**
     * 매칭 신청
     */
    @PostMapping("/apply")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "매칭 신청", description = "팀 리더가 매칭 글에 매칭을 신청한다.")
    public ResponseEntity<?> applyForMatch(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long teamId,
            @RequestParam Long matchPostId
    ) {
        User user = customUserDetails.getUser();
        matchRequestService.applyForMatch(user.getId(), teamId, matchPostId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("매칭 신청", null));
    }

    /**
     * 매칭 수락 / 거절
     */
    @Operation(summary = "매칭 수락/거절", description = "매칭 글을 올린 팀 리더가 매칭 수락 혹은 거절한다.")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{matchRequestId}/decide")
    public ResponseEntity<?> decideMatchRequest(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long matchRequestId,
            @RequestParam DecideStatus status
    ) {
        User user = customUserDetails.getUser();
        matchRequestService.decideMatchRequest(user.getId(), matchRequestId, status);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("매칭 수락/거절", null));
    }

    /**
     * 특정 팀의 매칭 요청 내역 조회
     */
    @GetMapping
    @Operation(summary = "매칭 요청 내역 조회", description = "특정 팀의 매칭 요청 내역을 조회한다.")
    public ResponseEntity<?> getMatchRequestList(
            @RequestParam Long teamId
    ) {
        List<MatchRequestResponse> result = matchRequestService.getMatchRequestList(teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("매칭 요청 내역 조회", result));
    }
}
