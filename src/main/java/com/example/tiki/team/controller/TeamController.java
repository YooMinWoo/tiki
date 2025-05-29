package com.example.tiki.team.controller;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import com.example.tiki.team.dto.*;
import com.example.tiki.team.service.TeamService;
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
@RequestMapping("/api/teams")
@Tag(name = "Team Controller", description = "팀 관련 기능")
public class TeamController {

    private final TeamService teamService;


    @GetMapping("/")
    @Operation(summary = "팀 목록 조회",
            description = "팀 전체 / 상태별 목록 조회 API"
    )
    public ResponseEntity<?> team(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "status", required = false) TeamStatusVisible teamStatusVisible) {
        List<TeamDto> teamList = teamService.getTeamSearchResult(keyword, teamStatusVisible);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 목록 조회", teamList));
    }

    @PostMapping("/{teamId}/inactive")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 비활성화",
            description = "팀 리더가 팀을 비활성화합니다."
    )
    public ResponseEntity<?> inactiveTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                  @PathVariable("teamId") Long teamId) {
        User user = customUserDetails.getUser();
        teamService.inactiveTeam(user.getId(), teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 비활성화 success!", null));
    }

    @PostMapping("/{teamId}/active")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 활성화",
            description = "팀 리더가 팀을 활성화합니다."
    )
    public ResponseEntity<?> activeTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @PathVariable("teamId") Long teamId) {
        User user = customUserDetails.getUser();
        teamService.activeTeam(user.getId(), teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 활성화 success!", null));
    }

    @PostMapping("/{teamId}/disband")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 해체",
            description = "팀 리더가 팀을 해체합니다."
    )
    public ResponseEntity<?> disbandTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                  @PathVariable("teamId") Long teamId) {
        User user = customUserDetails.getUser();
        teamService.disbandTeam(user.getId(), teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 해체 success!", null));
    }


    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 생성",
            description = "팀 생성 API"
    )
    public ResponseEntity<?> createTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @RequestBody TeamCreateRequestDto teamCreateRequestDto){
        User user = customUserDetails.getUser();
        teamService.createTeam(user.getId(), teamCreateRequestDto);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 생성 success", null));
    }

    @PostMapping("/{teamId}/join")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 가입하기",
            description = "팀 가입 API"
    )
    public ResponseEntity<?> teamJoinRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @PathVariable("teamId") Long teamId){
        User user = customUserDetails.getUser();
        teamService.teamJoinRequest(user, teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 가입 success", null));
    }

    @PostMapping("/{teamId}/join/{userId}/approve")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 가입 승인",
            description = "팀 가입 승인 API"
    )
    public ResponseEntity<?> approveTeamJoinRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                    @PathVariable("teamId") Long teamId,
                                                    @PathVariable("userId") Long userId){
        User user = customUserDetails.getUser();
        teamService.handleTeamUserAction(user.getId(), userId, teamId, TeamUserStatus.APPROVED);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 가입 승인 success", null));
    }

    @PostMapping("/{teamId}/join/{userId}/reject")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 가입 거절",
            description = "팀 가입 거절 API"
    )
    public ResponseEntity<?> rejectTeamJoinRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @PathVariable("teamId") Long teamId,
                                                   @PathVariable("userId") Long userId){
        User user = customUserDetails.getUser();
        teamService.handleTeamUserAction(user.getId(), userId, teamId, TeamUserStatus.REJECTED);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 가입 거절 success", null));
    }

    @PostMapping("/{teamId}/users/{userId}/kick")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 방출",
            description = "팀 방출 API"
    )
    public ResponseEntity<?> kickUserFromTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                    @PathVariable("teamId") Long teamId,
                                                    @PathVariable("userId") Long userId){
        User user = customUserDetails.getUser();
        teamService.handleTeamUserAction(user.getId(), userId, teamId, TeamUserStatus.KICKED);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 방출 success", null));
    }


    @DeleteMapping("/{teamId}/leave")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 탈퇴",
            description = "팀 탈퇴 API"
    )
    public ResponseEntity<?> leaveTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @PathVariable("teamId") Long teamId){
        User user = customUserDetails.getUser();
        teamService.requestTeamLeave(user, teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 탈퇴 success", null));
    }

    @DeleteMapping("/{teamId}/join-request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 가입 요청 취소",
            description = "팀 가입 요청 취소 API"
    )
    public ResponseEntity<?> cancelJoinRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                @PathVariable("teamId") Long teamId){
        User user = customUserDetails.getUser();
        teamService.cancelJoinRequest(user.getId(), teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 가입 요청 취소 success", null));
    }

    @GetMapping("/{teamId}/join-request/waiting")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 가입 대기 회원 리스트",
            description = "팀 가입 대기 회원 리스트 조회 API"
    )
    public ResponseEntity<?> getWaitingJoinRequests(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                               @PathVariable("teamId") Long teamId){
        User user = customUserDetails.getUser();
        List<TeamUserSimpleResponse> waitingJoinRequests = teamService.getWaitingJoinRequests(user.getId(), teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 가입 대기 회원 리스트", waitingJoinRequests));
    }

    @GetMapping("/{teamId}/users")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 회원 리스트",
            description = "팀 회원 리스트 조회 API"
    )
    public ResponseEntity<?> getTeamUsers(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                          @PathVariable("teamId") Long teamId){
        User user = customUserDetails.getUser();
        List<TeamUserSimpleResponse> getTeamUsers = teamService.getTeamUsers(user.getId(), teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 회원 리스트", getTeamUsers));
    }

    /**
     * 팀 리더 변경
     */
    @PostMapping("/{teamId}/change-leader/{afterLeaderId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 리더 변경", description = "기존 리더가 팀의 다른 팀원을 새로운 리더로 지정한다.")
    public ResponseEntity<?> changeTeamLeader(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("teamId") Long teamId,
            @PathVariable("afterLeaderId") Long afterLeaderId
    ) {
        Long beforeLeaderId = customUserDetails.getUser().getId();

        teamService.changeLeader(beforeLeaderId, afterLeaderId, teamId);
        return ResponseEntity.ok(ApiResponse.success("팀 리더 변경 성공", null));
    }


}
