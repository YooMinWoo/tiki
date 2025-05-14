package com.example.tiki.notifircation.controller;

import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.notifircation.dto.NotificationDto;
import com.example.tiki.notifircation.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "Notification Controller", description = "알림 관련 API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "알림 목록 조회", description = "사용자의 모든 알림 조회 API")
    public ResponseEntity<?> getNotifications(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getId();
        List<NotificationDto> notifications = notificationService.getNotification(userId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("알림 목록 조회 성공", notifications));
    }

    @PostMapping("/read-all")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "모든 알림 읽음 처리", description = "해당 사용자의 모든 알림을 읽음 처리합니다.")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("모든 알림 읽음 처리 완료", null));
    }

    @PostMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "단건 알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    public ResponseEntity<?> markAsRead(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @PathVariable Long notificationId) {
        Long userId = customUserDetails.getUser().getId();
        notificationService.markAsRead(userId, notificationId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("알림 읽음 처리 완료", null));
    }
}
