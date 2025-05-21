package com.example.tiki.team.service;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.global.exception.TeamApplicationException;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.entity.TeamUserHistory;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import com.example.tiki.team.dto.*;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserHistoryRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamUserHistoryRepository teamUserHistoryRepository;
    private final AuthRepository authRepository;
    private final NotificationRepository notificationRepository;

    // 팀 해체
    @Transactional
    public void disbandTeam(Long userId, Long teamId) {
        // 리더인지 확인
        validateLeaderAuthority(userId, teamId);

        // 팀 상태 변경 -> disband
        Team team = getOrElseThrow(teamId);
        team.changeStatus(TeamStatus.DISBANDED);

        List<TeamUserStatus> teamUserStatuses = List.of(TeamUserStatus.APPROVED, TeamUserStatus.WAITING);

        // 팀원들 조회 (approved)
        List<TeamUser> approvedAndWaitingTeamUsers = teamUserRepository.findByTeamIdAndTeamUserStatusIn(teamId, teamUserStatuses);

        for (TeamUser teamUser : approvedAndWaitingTeamUsers) {
            TeamUserRole previousRole = teamUser.getTeamUserRole();
            TeamUserStatus previousStatus = teamUser.getTeamUserStatus();

            // 팀원들의 상태를 disbanded로 변경
            teamUser.changeStatus(TeamUserStatus.DISBANDED);

            // 팀이 해체되었다고 알림 전송
            sendNotification(teamUser.getUserId(), team.getTeamName(), TeamUserStatus.DISBANDED, teamId);

            // history 저장
            saveHistory(teamUser, previousRole, previousStatus);
        }

    }

    // 내 승인 대기 조회
    public List<MyWaiting> getMyWaiting(Long userId) {
        List<MyWaiting> myWaitings = new ArrayList<>();
        List<TeamUser> teamUserList = teamUserRepository.findByUserIdAndTeamUserStatus(userId, TeamUserStatus.WAITING);
        for (TeamUser teamUser : teamUserList) {
            Team team = teamRepository.findById(teamUser.getTeamId())
                    .orElseThrow(() -> new NotFoundException("팀을 찾을 수 없습니다."));
            myWaitings.add(MyWaiting.from(team, teamUser));
        }

        return myWaitings;
    }

    // 내 팀 조회
    public List<MyTeam> getMyTeam(Long userId){
        List<MyTeam> myTeams = new ArrayList<>();
        List<TeamUser> teamUsers = teamUserRepository.findByUserIdAndTeamUserStatus(userId, TeamUserStatus.APPROVED);
        for (TeamUser teamUser : teamUsers) {
            Team team = teamRepository.findById(teamUser.getTeamId())
                    .orElseThrow(() -> new NotFoundException("팀을 찾을 수 없습니다."));
            int memberCount = teamUserRepository.findAllByTeamIdAndTeamUserStatus(teamUser.getTeamId(), TeamUserStatus.APPROVED).size();
            myTeams.add(MyTeam.from(team, memberCount, teamUser));
        }
        return myTeams;
    }

    // 팀 조회
    public List<TeamDto> findTeams(TeamStatus teamStatus) {
        List<Team> teams;
        if (teamStatus == null) {
            teams = teamRepository.findAll();
        } else {
            teams = teamRepository.findByTeamStatus(teamStatus);
        }
        return Optional.ofNullable(teams)
                .orElse(Collections.emptyList())
                .stream()
                .map(TeamDto::toDto)
                .collect(Collectors.toList());
    }



    // 팀 생성
    @Transactional
    public void createTeam(Long userId, TeamCreateRequestDto teamCreateRequestDto) {
        Team team = Team.builder()
                    .teamName(teamCreateRequestDto.getTeamName())
                    .teamDescription(teamCreateRequestDto.getTeamDescription())
                    .teamStatus(TeamStatus.OPEN)
                    .build();

        teamRepository.save(team);

        TeamUser teamUser = TeamUser.builder()
                            .userId(userId)
                            .teamId(team.getId())
                            .teamUserRole(TeamUserRole.ROLE_LEADER)
                            .teamUserStatus(TeamUserStatus.APPROVED)
                            .build();

        teamUserRepository.save(teamUser);

        // history 저장
        TeamUserHistory teamUserHistory = TeamUserHistory.builder()
                .teamUserId(teamUser.getId())
                .userId(userId)
                .teamId(team.getId())
                .previousRole(null)
                .currentRole(TeamUserRole.ROLE_LEADER)
                .previousStatus(null)
                .currentStatus(TeamUserStatus.APPROVED)
                .build();

        teamUserHistoryRepository.save(teamUserHistory);
    }

    // 팀 가입 요청
    @Transactional
    public TeamUser teamJoinRequest(User user, Long teamId) {
        Team team = getOrElseThrow(teamId);

        if (team.getTeamStatus() == TeamStatus.CLOSED) {
            throw new TeamApplicationException("해당 팀은 모집이 만료되었습니다.");
        }
        if (team.getTeamStatus() == TeamStatus.DISBANDED) {
            throw new TeamApplicationException("해당 팀은 존재하지 않습니다.");
        }

        Optional<TeamUser> optionalTeamUser = teamUserRepository.findByUserIdAndTeamId(user.getId(), teamId);

        if (optionalTeamUser.isPresent()) {
            TeamUser existingTeamUser = optionalTeamUser.get();

            if (existingTeamUser.getTeamUserStatus() == TeamUserStatus.APPROVED) {
                throw new TeamApplicationException("이미 소속된 팀입니다.");
            }

            if (existingTeamUser.getTeamUserStatus() == TeamUserStatus.WAITING) {
                throw new TeamApplicationException("이미 지원한 팀입니다.");
            }

        }
        // 기존 TeamUser가 있으나 상태가 탈퇴/방출 등인 경우 → 새 신청 허용
        // 새 신청 생성
        TeamUser teamUser = TeamUser.builder()
                .userId(user.getId())
                .teamId(teamId)
                .teamUserRole(TeamUserRole.ROLE_MEMBER)
                .teamUserStatus(TeamUserStatus.WAITING)
                .build();

        teamUserRepository.save(teamUser);

        // 감독 id 가져오기
        TeamUser leaderTeamUser = teamUserRepository.findByTeamIdAndTeamUserRole(teamId, TeamUserRole.ROLE_LEADER);

        // history 저장
        TeamUserHistory teamUserHistory = TeamUserHistory.builder()
                .teamUserId(teamUser.getId())
                .userId(user.getId())
                .teamId(team.getId())
                .previousRole(null)
                .currentRole(null)
                .previousStatus(null)
                .currentStatus(TeamUserStatus.WAITING)
                .build();

        teamUserHistoryRepository.save(teamUserHistory);

        notificationRepository.save(Notification.builder()
                .userId(leaderTeamUser.getUserId())
                .message(user.getId() + "(" + user.getEmail() + ")님께서 가입 요청을 보냈습니다.")
                .notificationType(NotificationType.JOIN)
                .targetId(user.getId())
                .build());

        return teamUser;
    }

    // 승인/거절/방출
    @Transactional
    public void handleTeamUserAction(Long leaderId, Long userId, Long teamId, TeamUserStatus teamUserStatus){
        // 팀 존재 확인
        Team team = getOrElseThrow(teamId);

        // 리더 확인
        validateLeaderAuthority(leaderId, teamId);

        // status 확인
        TeamUser teamUser = getTargetTeamUser(userId, teamId, teamUserStatus);

        TeamUserStatus previousStatus = teamUser.getTeamUserStatus();
        TeamUserRole previousRole = teamUser.getTeamUserRole();

        if (teamUserStatus == TeamUserStatus.KICKED && previousRole == TeamUserRole.ROLE_LEADER) {
            throw new TeamApplicationException("처리가 불가능합니다.");
        }

        // 상태 변경 ( 대기 -> 승인 / 대기 -> 거절 / 승인 -> 방출 )
        teamUser.changeStatus(teamUserStatus);

        // history 저장
        saveHistory(teamUser, previousRole, previousStatus);

        // 알림 발송
        sendNotification(userId, team.getTeamName(), teamUserStatus, teamId);
    }

    // 탈퇴
    @Transactional
    public void requestTeamLeave(User user, Long teamId){
        // 팀 존재 확인
        getOrElseThrow(teamId);

        // 팀 소속인지 확인
        TeamUser teamUser = getTeamUserWithStatus(user.getId(), teamId, TeamUserStatus.APPROVED);

        // if 권한이 리더 -> throw
        if(teamUser.getTeamUserRole() == TeamUserRole.ROLE_LEADER){
            throw new TeamApplicationException("감독을 다른 사람에게 위임한 뒤 탈퇴하여 주세요.");
        }

        // 이전 상태
        TeamUserStatus previousStatus = teamUser.getTeamUserStatus();

        // 이전 권한
        TeamUserRole previousRole = teamUser.getTeamUserRole();

        // get 리더 ID
        TeamUser leader = teamUserRepository.findByTeamIdAndTeamUserRole(teamId, TeamUserRole.ROLE_LEADER);

        // 탈퇴하기
        teamUser.changeStatus(TeamUserStatus.LEFT);

        // 히스토리 남기기
        saveHistory(teamUser, previousRole, previousStatus);

        // 리더에게 특정 회원이 탈퇴했다고 알림 전송
        notificationRepository.save(
                Notification.builder()
                        .userId(leader.getUserId())
                        .message(user.getName()+"("+user.getEmail()+")님께서 탈퇴했습니다.")
                        .notificationType(NotificationType.LEFT)
                        .targetId(user.getId())
                        .build()
        );
    }

    // 가입 요청 취소
    @Transactional
    public void cancelJoinRequest(Long userId, Long teamId) {
        // 취소가 가능한 waiting인 TeamUser의 TeamUserHistory는 1개밖에 없을 것이다.
        TeamUser teamUser = getTeamUserWithStatus(userId, teamId, TeamUserStatus.WAITING);
        teamUserRepository.delete(teamUser);

        teamUserHistoryRepository.deleteAllByTeamUserId(teamUser.getId());
    }

    // 승인 대기 리스트
    public List<TeamUserSimpleResponse> getWaitingJoinRequests(Long userId, Long teamId){
        // 팀 존재 확인
        getOrElseThrow(teamId);

        // 권한 확인
        validateLeaderAuthority(userId, teamId);

        List<TeamUser> teamUsers = teamUserRepository.findAllByTeamIdAndTeamUserStatus(teamId, TeamUserStatus.WAITING);

        return convertToSimpleResponses(teamUsers);
    }

    // 회원 리스트
    public List<TeamUserSimpleResponse> getTeamUsers(Long userId, Long teamId){
        // 팀 존재 확인
        getOrElseThrow(teamId);

        List<TeamUser> teamUsers = teamUserRepository.findAllByTeamIdAndTeamUserStatus(teamId, TeamUserStatus.APPROVED);
        return convertToSimpleResponses(teamUsers);
    }

    // 현재 처리 상태 확인
    private TeamUser getTargetTeamUser(Long userId, Long teamId, TeamUserStatus targetStatus) {
        return switch (targetStatus) {
            case APPROVED, REJECTED -> getTeamUserWithStatus(userId, teamId, TeamUserStatus.WAITING);
            case KICKED -> getTeamUserWithStatus(userId, teamId, TeamUserStatus.APPROVED);
            default -> throw new IllegalArgumentException("처리할 수 없는 상태입니다.");
        };
    }

    // 알림 발송
    private void sendNotification(Long userId, String teamName, TeamUserStatus status, Long teamId) {
        NotificationType type = switch (status) {
            case APPROVED -> NotificationType.APPROVE;
            case REJECTED -> NotificationType.REJECT;
            case KICKED -> NotificationType.KICK;
            case DISBANDED -> NotificationType.DISBAND;
            default -> null;
        };

        if (type != null) {
            notificationRepository.save(Notification.builder()
                    .userId(userId)
                    .message(teamName + getMessageByStatus(status))
                    .notificationType(type)
                    .targetId(teamId)
                    .build());
        }
    }

    // 상태에 따른 알림 메시지 지정
    private String getMessageByStatus(TeamUserStatus status) {
        return switch (status) {
            case APPROVED -> "팀에서 가입을 수락했습니다.";
            case REJECTED -> "팀에서 가입을 거절했습니다.";
            case KICKED -> "팀에서 방출되었습니다.";
            case DISBANDED -> "팀이 해체하였습니다.";
            default -> "";
        };
    }

    // 이력 저장
    private void saveHistory(TeamUser teamUser, TeamUserRole previousRole, TeamUserStatus previousStatus) {
        teamUserHistoryRepository.save(TeamUserHistory.builder()
                .teamUserId(teamUser.getId())
                .userId(teamUser.getUserId())
                .teamId(teamUser.getTeamId())
                .previousRole(previousRole)
                .currentRole(teamUser.getTeamUserRole())
                .previousStatus(previousStatus)
                .currentStatus(teamUser.getTeamUserStatus())
                .build());
    }

    // 특정 상태의 유저 확인
    private TeamUser getTeamUserWithStatus(Long userId, Long teamId, TeamUserStatus teamUserStatus) {
        TeamUser teamUser = teamUserRepository.findByUserIdAndTeamIdAndTeamUserStatus(userId, teamId, teamUserStatus)
                .orElseThrow(() -> new ForbiddenException("해당 상태의 유저를 찾을 수 없습니다."));
        return teamUser;
    }

    // 수행하려는 주체의 권한이 리더인지 확인
    private void validateLeaderAuthority(Long leaderId, Long teamId) {
        if(leaderId != teamUserRepository.findByTeamIdAndTeamUserRole(teamId, TeamUserRole.ROLE_LEADER).getUserId()){
            throw new ForbiddenException("해당 작업을 수행할 권한이 없습니다.");
        }
    }

    // 팀이 존재하는지 확인
    private Team getOrElseThrow(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ForbiddenException("해당 팀은 존재하지 않습니다."));
    }

    // List<TeamUser> -> List<TeamUserSimpleResponse>
    private List<TeamUserSimpleResponse> convertToSimpleResponses(List<TeamUser> teamUsers) {
        return teamUsers.stream()
                .map(teamUser -> {
                    User user = authRepository.findById(teamUser.getUserId())
                            .orElseThrow(() -> new NotFoundException("유저를 불러오는 중 에러가 발생하였습니다."));
                    return TeamUserSimpleResponse.from(user, teamUser);
                })
                .collect(Collectors.toList());
    }

}
