package com.example.tiki.team.service;

import com.example.tiki.auth.domain.User;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import com.example.tiki.team.dto.*;

import java.util.List;

public interface TeamService {
    // 내 승인 대기 조회
    List<MyWaiting> getMyWaiting(Long userId);

    // 내 팀 조회
    List<MyTeam> getMyTeam(Long userId, TeamStatus status);

    // 팀 조회
    List<TeamDto> getTeamSearchResult(String keyword, TeamStatusVisible teamStatusVisible);

    // 팀 생성
    void createTeam(Long userId, TeamCreateRequestDto teamCreateRequestDto);

    // 팀 가입 요청
    TeamUser teamJoinRequest(User user, Long teamId);

    // 승인/거절/방출
    void handleTeamUserAction(Long leaderId, Long userId, Long teamId, TeamUserStatus teamUserStatus);

    // 탈퇴
    void requestTeamLeave(User user, Long teamId);

    // 가입 요청 취소
    void cancelJoinRequest(Long userId, Long teamId);

    // 승인 대기 리스트
    List<TeamUserSimpleResponse> getWaitingJoinRequests(Long userId, Long teamId);

    // 회원 리스트
    List<TeamUserSimpleResponse> getTeamUsers(Long userId, Long teamId);

    // 팀 해체
    void disbandTeam(Long userId, Long teamId);

    // 팀 비활성화
    void inactiveTeam(Long userId, Long teamId);

    // 팀 활성화
    void activeTeam(Long userId, Long teamId);

}
