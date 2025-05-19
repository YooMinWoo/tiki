package com.example.tiki.team.controller;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.security.user.CustomUserDetails;
import com.example.tiki.global.dto.ApiResponse;
import com.example.tiki.team.dto.TeamCreateRequestDto;
import com.example.tiki.team.dto.TeamDto;
import com.example.tiki.team.dto.TeamUserSimpleResponse;
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
@RequestMapping("/api")
@Tag(name = "Team Controller", description = "팀 관련 기능")
public class TeamController {

    private final TeamService teamService;


    @GetMapping("/teams")
    @Operation(summary = "팀 목록 조회",
            description = "팀 목록 조회 API"
    )
    public ResponseEntity<?> team() {
        List<TeamDto> teamList = teamService.findTeamList();
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 전체 목록 조회", teamList));
    }

    @PostMapping("/teams")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 등록",
            description = "팀 등록 API"
    )
    public ResponseEntity<?> createTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @RequestBody TeamCreateRequestDto teamCreateRequestDto){
        User user = customUserDetails.getUser();
        teamService.createTeam(user.getId(), teamCreateRequestDto);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 등록 ok", null));
    }

    @PostMapping("/teams/{teamId}/join")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 가입하기",
            description = "팀 가입 API"
    )
    public ResponseEntity<?> teamJoinRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @PathVariable("teamId") Long teamId){
        User user = customUserDetails.getUser();
        teamService.teamJoinRequest(user, teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 가입 ok", null));
    }

    @PostMapping("/teams/{teamId}/join/{userId}/approve")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 가입 승인",
            description = "팀 가입 승인 API"
    )
    public ResponseEntity<?> approveTeamJoinRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                    @PathVariable("teamId") Long teamId,
                                                    @PathVariable("userId") Long userId){
        User user = customUserDetails.getUser();
        teamService.approveTeamJoinRequest(user.getId(), userId, teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 가입 승인 ok", null));
    }

    @PostMapping("/teams/{teamId}/join/{userId}/reject")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 가입 거절",
            description = "팀 가입 거절 API"
    )
    public ResponseEntity<?> rejectTeamJoinRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                   @PathVariable("teamId") Long teamId,
                                                   @PathVariable("userId") Long userId){
        User user = customUserDetails.getUser();
        teamService.rejectTeamJoinRequest(user.getId(), userId, teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 가입 거절 ok", null));
    }

    @DeleteMapping("/teams/{teamId}/users/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 방출",
            description = "팀 방출 API"
    )
    public ResponseEntity<?> kickUserFromTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                    @PathVariable("teamId") Long teamId,
                                                    @PathVariable("userId") Long userId){
        User user = customUserDetails.getUser();
        teamService.kickUserFromTeam(user.getId(), userId, teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 방출 ok", null));
    }


    @DeleteMapping("/teams/{teamId}/leave")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 탈퇴",
            description = "팀 탈퇴 API"
    )
    public ResponseEntity<?> leaveTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @PathVariable("teamId") Long teamId){
        User user = customUserDetails.getUser();
        teamService.leaveTeam(user, teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 탈퇴 ok", null));
    }

    @DeleteMapping("/teams/{teamId}/join-request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "팀 가입 요청 취소",
            description = "팀 가입 요청 취소 API"
    )
    public ResponseEntity<?> cancelJoinRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                @PathVariable("teamId") Long teamId){
        User user = customUserDetails.getUser();
        teamService.cancelJoinRequest(user.getId(), teamId);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ApiResponse.success("팀 가입 요청 취소 ok", null));
    }

    @GetMapping("/teams/{teamId}/join-request/waiting")
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

    @GetMapping("/teams/{teamId}/users")
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
}
