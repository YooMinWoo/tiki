package com.example.tiki.notice.controller;

import com.example.tiki.auth.domain.User;
import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.notice.dto.CreateNoticeRequest;
import com.example.tiki.notice.dto.NoticeDetailDto;
import com.example.tiki.notice.dto.NoticeListDto;
import com.example.tiki.notice.dto.SearchNoticeCondition;
import com.example.tiki.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
@Tag(name = "공지사항 API", description = "공지사항 등록, 조회, 수정, 삭제 기능 제공")
public class NoticeController {

    private final NoticeService noticeService;

    // 🔸 공지사항 등록
    @PostMapping
    @Operation(summary = "공지사항 등록", description = "관리자가 공지사항을 등록합니다.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createNotion(@AuthenticationPrincipal User user,
                                          @Valid @RequestBody CreateNoticeRequest request) {
        noticeService.createNotion(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("공지사항 등록 완료", null));

    }

    // 🔸 공지사항 리스트 조회
    @GetMapping
    @Operation(summary = "공지사항 목록 조회", description = "공지사항 전체 리스트를 조회합니다.")
    public ResponseEntity<?> getNotionList(@ModelAttribute SearchNoticeCondition condition) {
        List<NoticeListDto> result = noticeService.getNotionList(condition);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("공지사항 리스트 조회 성공", result));
    }

    // 🔸 공지사항 상세 조회
    @GetMapping("/{notionId}")
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 하나의 상세 내용을 조회합니다.")
    public ResponseEntity<?> getNotionDetail(@PathVariable Long notionId) {
        NoticeDetailDto detail = noticeService.getNotionDetail(notionId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("공지사항 상세 조회 성공", detail));
    }

    // 🔸 공지사항 수정
    @PutMapping("/{notionId}")
    @Operation(summary = "공지사항 수정", description = "공지사항을 수정합니다.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateNotion(@PathVariable Long notionId,
                                          @Valid @RequestBody CreateNoticeRequest request) {
        noticeService.updateNotion(request, notionId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("공지사항 수정 완료", null));
    }

    // 🔸 공지사항 삭제
    @DeleteMapping("/{notionId}")
    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteNotion(@PathVariable Long notionId) {
        noticeService.deleteNotion(notionId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("공지사항 삭제 완료", null));
    }
}
