package com.example.tiki.recruitment.controller;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.recruitment.dto.*;
import com.example.tiki.recruitment.service.RecruitmentService;
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
@RequestMapping("/api/recruitments")
@Tag(name = "Recruitment Controller", description = "모집 게시글 관련 API")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    // 모집글 등록
    @PostMapping("/teams/{teamId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "모집글 생성", description = "팀 리더가 모집글을 생성한다.")
    public ResponseEntity<?> createRecruitmentPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @PathVariable("teamId") Long teamId,
                                                   @RequestBody RecruitmentCreateRequest request) {
        User user = customUserDetails.getUser();
        recruitmentService.createRecruitmentPost(user.getId(), teamId, request);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 등록 success!", null));
    }

    // 모집글 수정
    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "모집글 수정", description = "팀 리더가 모집글을 수정한다.")
    public ResponseEntity<?> updateRecruitmentPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @RequestBody RecruitmentUpdateRequest request) {
        User user = customUserDetails.getUser();
        recruitmentService.updateRecruitmentPost(user.getId(), request);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 수정 success!", null));
    }

    // 모집글 마감
    @PutMapping("/{recruitmentId}/close")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "모집글 마감", description = "팀 리더가 모집글을 마감한다.")
    public ResponseEntity<?> closeRecruitmentPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                  @PathVariable Long recruitmentId) {
        User user = customUserDetails.getUser();
        recruitmentService.closeRecruitmentPost(user.getId(), recruitmentId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 마감 success!", null));
    }

    // 모집글 삭제
    @DeleteMapping("/{recruitmentId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "모집글 삭제", description = "팀 리더가 모집글을 삭제한다.")
    public ResponseEntity<?> deleteRecruitmentPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @PathVariable Long recruitmentId) {
        User user = customUserDetails.getUser();
        recruitmentService.deleteRecruitmentPost(user, recruitmentId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 삭제 success!", null));
    }

    // 모집글 리오픈
    @PutMapping("/{recruitmentId}/reopen")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "모집글 리오픈", description = "팀 리더가 모집글을 리오픈한다.")
    public ResponseEntity<?> reopenRecruitmentPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @PathVariable Long recruitmentId) {
        User user = customUserDetails.getUser();
        recruitmentService.reopenRecruitmentPost(user.getId(), recruitmentId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 리오픈 success!", null));
    }

    // 모집글 조회 (키워드, 상태 필터)
    @GetMapping("/search")
    @Operation(summary = "모집글 조회", description = "필터에 맞는 모집글을 조회한다. 입력 X -> 전체 조회")
    public ResponseEntity<?> getRecruitmentSearchResult(
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) RecruitmentStatusVisible status) {
        List<RecruitmentSearchResultDto> results = recruitmentService.getRecruitmentSearchResult(keyword, status);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 조회 success!", results));
    }

    // 모집글 상세 조회
    @GetMapping("/{recruitmentId}")
    @Operation(summary = "모집글 상세 조회", description = "모집글 상세 조회")
    public ResponseEntity<?> getRecruitmentDetail(
            @PathVariable("recruitmentId") Long recruitmentId) {
        RecruitmentDetailDto recruitmentDetail = recruitmentService.getRecruitmentDetail(recruitmentId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 상세 조회 success!", recruitmentDetail));
    }

}
