package com.example.tiki.auth.controller;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.dto.MyPageDto;
import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.team.dto.MyTeam;
import com.example.tiki.team.dto.MyWaiting;
import com.example.tiki.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "Auth Controller", description = "내 정보 관련 기능")
public class MyPageController {

    private final TeamService teamService;

    // 내 승인 대기 조회
    @GetMapping("/teams/waiting")
    @Operation(summary = "내 승인 대기 팀 조회", description = "현재 내가 승인 대기 상태인 팀 목록을 조회합니다.")
    public ResponseEntity<?> getMyWaiting(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        List<MyWaiting> waitingList = teamService.getMyWaiting(user.getId());
        return ResponseEntity.ok(ApiResponse.success("승인 대기 중인 팀 목록 조회", waitingList));
    }

    // 내 팀 조회
    @GetMapping("/teams")
    @Operation(summary = "내 팀 목록 조회", description = "현재 내가 소속된 팀 목록을 조회합니다.")
    public ResponseEntity<?> getMyTeams(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        List<MyTeam> teamList = teamService.getMyTeam(user.getId());
        return ResponseEntity.ok(ApiResponse.success("내 팀 목록 조회", teamList));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    @Operation(summary = "마이페이지",
            description = "마이페이지 조회 API (로그인 했을 경우에만 접속 가능)"
    )
    public ResponseEntity<?> myPage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        MyPageDto myPageDto = MyPageDto.toDto(customUserDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("my!", myPageDto));
    }
}
