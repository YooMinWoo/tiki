package com.example.tiki.match.controller;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.match.dto.*;
import com.example.tiki.match.service.MatchPostService;
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
@RequestMapping("/api/match-posts")
@Tag(name = "MatchPost Controller", description = "매칭 게시글 관련 API")
public class MatchPostController {

    private final MatchPostService matchPostService;

    /**
     * 매칭글 생성
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "매칭글 생성", description = "팀 리더가 매칭글을 생성한다.")
    public ResponseEntity<?> createMatchPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MatchPostRequest request
    ) {
        User user = customUserDetails.getUser();
        matchPostService.createMatchPost(user.getId(), request);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("매칭글 생성 success!", null));
    }

    /**
     * 매칭글 수정
     */
    @PutMapping("/{matchPostId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "매칭글 수정", description = "팀 리더가 매칭글을 수정한다.")
    public ResponseEntity<?> updateMatchPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long matchPostId,
            @RequestBody MatchPostRequest request
    ) {
        User user = customUserDetails.getUser();
        matchPostService.updateMatchPost(user.getId(), matchPostId, request);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("매칭글 수정 success!", null));
    }

    /**
     * 매칭글 삭제
     */
    @DeleteMapping("/{matchPostId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "매칭글 삭제", description = "팀 리더 혹은 관리자가 매칭글을 삭제한다.")
    public ResponseEntity<?> deleteMatchPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long matchPostId
    ) {
        User user = customUserDetails.getUser();
        matchPostService.deleteMatchPost(user, matchPostId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("매칭글 삭제 success!", null));
    }

    /**
     * 매칭글 상세 조회
     */
    @GetMapping("/{matchPostId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "매칭글 상세 조회", description = "매칭글의 상세 정보를 조회한다.")
    public ResponseEntity<?> getMatchPostDetail(
            @PathVariable Long matchPostId
    ) {
        MatchPostResponse matchPostDetail = matchPostService.getMatchPostDetail(matchPostId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("매칭글 상세 조회", matchPostDetail));
    }

    /**
     * 매칭글 검색
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "매칭글 검색", description = "조건에 따라 매칭글을 검색한다.")
    public ResponseEntity<?> searchMatchPost(
            MatchPostSearchCondition condition
    ) {
        List<MatchPostSearchResponse> matchPostSearchResponses = matchPostService.searchMatchPost(condition);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("매칭글 검색", matchPostSearchResponses));
    }

    /**
     * 특정 팀의 매칭글 내역 조회
     */
    @GetMapping("/by-team/{teamId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 별 매칭글 조회", description = "특정 팀이 올린 매칭글 목록을 조회한다.")
    public ResponseEntity<?> searchMatchPostByTeam(
            @PathVariable Long teamId,
            MatchPostByTeamSearchCondition condition
    ) {
        List<MatchPostSearchResponse> matchPostSearchResponses = matchPostService.searchMatchPostByTeam(teamId, condition);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 별 매칭글 조회", matchPostSearchResponses));
    }

    /**
     * 매칭 취소
     */
    @PostMapping("/{matchPostId}/cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "매칭 취소", description = "매칭이 성사된 후 팀 리더가 매칭을 취소한다.")
    public ResponseEntity<?> cancelMatch(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long matchPostId
    ) {
        User user = customUserDetails.getUser();
        matchPostService.cancelMatch(user.getId(), matchPostId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("매칭 취소 success!", null));
    }
}