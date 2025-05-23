package com.example.tiki.recruitment.controller;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.recruitment.dto.RecruitmentCreateRequest;
import com.example.tiki.recruitment.dto.RecruitmentSearchResultDto;
import com.example.tiki.recruitment.dto.RecruitmentStatusVisible;
import com.example.tiki.recruitment.dto.RecruitmentUpdateRequest;
import com.example.tiki.recruitment.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitments")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    // 모집글 등록
    @PostMapping("/teams/{teamId}")
    public ResponseEntity<?> createRecruitmentPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @PathVariable("teamId") Long teamId,
                                                   @RequestBody RecruitmentCreateRequest request) {
        User user = customUserDetails.getUser();
        recruitmentService.createRecruitmentPost(user.getId(), teamId, request);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 등록 success!", null));
    }

    // 모집글 수정
    @PutMapping
    public ResponseEntity<?> updateRecruitmentPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @RequestBody RecruitmentUpdateRequest request) {
        User user = customUserDetails.getUser();
        recruitmentService.updateRecruitmentPost(user.getId(), request);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 수정 success!", null));
    }

    // 모집글 마감
    @PutMapping("/{recruitmentId}/close")
    public ResponseEntity<?> closeRecruitmentPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                  @PathVariable Long recruitmentId) {
        User user = customUserDetails.getUser();
        recruitmentService.closeRecruitmentPost(user.getId(), recruitmentId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 마감 success!", null));
    }

    // 모집글 삭제
    @DeleteMapping("/{recruitmentId}")
    public ResponseEntity<?> deleteRecruitmentPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @PathVariable Long recruitmentId) {
        User user = customUserDetails.getUser();
        recruitmentService.deleteRecruitmentPost(user.getId(), recruitmentId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 삭제 success!", null));
    }

    // 모집글 리오픈
    @PutMapping("/{recruitmentId}/reopen")
    public ResponseEntity<?> reopenRecruitmentPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @PathVariable Long recruitmentId) {
        User user = customUserDetails.getUser();
        recruitmentService.reopenRecruitmentPost(user.getId(), recruitmentId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 리오픈 success!", null));
    }

    // 모집글 조회 (키워드, 상태 필터)
    @GetMapping("/search")
    public ResponseEntity<?> getRecruitmentSearchResult(
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) RecruitmentStatusVisible status) {
        List<RecruitmentSearchResultDto> results = recruitmentService.getRecruitmentSearchResult(keyword, status);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모집글 조회 success!", results));
    }
}
