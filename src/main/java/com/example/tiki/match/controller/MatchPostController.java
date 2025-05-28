package com.example.tiki.match.controller;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.match.dto.*;
import com.example.tiki.match.service.MatchPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "매칭글 생성", description = "팀 리더가 매칭글을 생성한다.")
    public ResponseEntity<Void> createMatchPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MatchPostRequest request
    ) {
        User user = customUserDetails.getUser();
        matchPostService.createMatchPost(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    /**
     * 매칭글 수정
     */
    @PutMapping("/{matchPostId}")
    @Operation(summary = "매칭글 수정", description = "팀 리더가 매칭글을 수정한다.")
    public ResponseEntity<Void> updateMatchPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long matchPostId,
            @RequestBody MatchPostRequest request
    ) {
        User user = customUserDetails.getUser();
        matchPostService.updateMatchPost(user.getId(), matchPostId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 매칭글 삭제
     */
    @DeleteMapping("/{matchPostId}")
    @Operation(summary = "매칭글 삭제", description = "팀 리더가 매칭글을 삭제한다.")
    public ResponseEntity<Void> deleteMatchPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long matchPostId
    ) {
        User user = customUserDetails.getUser();
        matchPostService.deleteMatchPost(user.getId(), matchPostId);
        return ResponseEntity.ok().build();
    }

    /**
     * 매칭글 상세 조회
     */
    @GetMapping("/{matchPostId}")
    @Operation(summary = "매칭글 상세 조회", description = "매칭글의 상세 정보를 조회한다.")
    public ResponseEntity<MatchPostResponse> getMatchPostDetail(
            @PathVariable Long matchPostId
    ) {
        return ResponseEntity.ok(matchPostService.getMatchPostDetail(matchPostId));
    }

    /**
     * 매칭글 검색
     */
    @GetMapping("/search")
    @Operation(summary = "매칭글 검색", description = "조건에 따라 매칭글을 검색한다.")
    public ResponseEntity<List<MatchPostSearchResponse>> searchMatchPost(
            MatchPostSearchCondition condition
    ) {
        return ResponseEntity.ok(matchPostService.searchMatchPost(condition));
    }

    /**
     * 특정 팀의 매칭글 내역 조회
     */
    @GetMapping("/by-team/{teamId}")
    @Operation(summary = "팀 별 매칭글 조회", description = "특정 팀이 올린 매칭글 목록을 조회한다.")
    public ResponseEntity<List<MatchPostSearchResponse>> searchMatchPostByTeam(
            @PathVariable Long teamId,
            MatchPostByTeamSearchCondition condition
    ) {
        return ResponseEntity.ok(matchPostService.searchMatchPostByTeam(teamId, condition));
    }

    /**
     * 매칭 취소
     */
    @PostMapping("/{matchPostId}/cancel")
    @Operation(summary = "매칭 취소", description = "매칭이 성사된 후 팀 리더가 매칭을 취소한다.")
    public ResponseEntity<Void> cancelMatch(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long matchPostId
    ) {
        User user = customUserDetails.getUser();
        matchPostService.cancelMatch(user.getId(), matchPostId);
        return ResponseEntity.ok().build();
    }
}