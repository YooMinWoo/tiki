package com.example.tiki.team.service;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.global.exception.TeamApplicationException;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.team.domain.Team;
import com.example.tiki.team.domain.TeamRole;
import com.example.tiki.team.domain.TeamStatus;
import com.example.tiki.team.domain.TeamUser;
import com.example.tiki.team.dto.TeamCreateRequestDto;
import com.example.tiki.team.dto.TeamDto;
import com.example.tiki.team.dto.TeamUserSimpleResponse;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final AuthRepository authRepository;
    private final NotificationRepository notificationRepository;

    // 전체 팀 조회
    public List<TeamDto> findTeamList() {
        return Optional.ofNullable(teamRepository.findAll())
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
                            .teamRole(TeamRole.ROLE_LEADER)
                            .build();

        teamUserRepository.save(teamUser);
    }

    // 팀 가입 요청
    @Transactional
    public TeamUser teamJoinRequest(User user, Long teamId){
        Team team = teamRepository.findById(teamId).
                orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));

        if(team.getTeamStatus() == TeamStatus.CLOSED) {
            throw new TeamApplicationException("해당 팀은 모집이 만료되었습니다.");
        }

        TeamUser teamUser = teamUserRepository.findLatestByUserIdAndTeamId(user.getId(), teamId).orElse(null);

        if(teamUser == null || teamUser.getTeamRole() == TeamRole.ROLE_INIT){
            Long leaderId = teamUserRepository.findLeaderUserIdByTeamId(teamId)
                    .orElseThrow(() -> new NotFoundException("에러가 발생하였습니다."));

            notificationRepository.save(
                    Notification.builder()
                            .userId(leaderId)
                            .message(user.getName()+"님께서 가입 요청을 보냈습니다.")
                            .notificationType(NotificationType.JOIN)
                            .targetId(user.getId())
                            .build()
            );
            return teamUserRepository.save(TeamUser.builder()
                    .userId(user.getId())
                    .teamId(teamId)
                    .teamRole(TeamRole.ROLE_WAITING)
                    .build());
        }
        Set<TeamRole> blockedRoles = Set.of(TeamRole.ROLE_LEFT, TeamRole.ROLE_KICKED, TeamRole.ROLE_REJECTED);

        if (blockedRoles.contains(teamUser.getTeamRole())) {
            throw new TeamApplicationException("해당 팀에는 지원할 수 없습니다.");
        }

        if (teamUser.getTeamRole() == TeamRole.ROLE_WAITING) {
            throw new TeamApplicationException("이미 지원한 팀입니다.");
        }

        throw new TeamApplicationException("이미 소속된 팀입니다.");

    }

    // 승인
    @Transactional
    public void approveTeamJoinRequest(Long leaderId, Long userId, Long teamId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("해당 팀은 존재하지 않습니다."));

        validateLeaderAuthority(leaderId, teamId);

        TeamUser teamUser = getLatestTeamUserOrThrow(userId, teamId, "대기 중인 내역이 없습니다.");

        if(teamUser.getTeamRole() != TeamRole.ROLE_WAITING){
            throw new TeamApplicationException("수락할 수 없습니다.");
        }

        notificationRepository.save(
                Notification.builder()
                        .userId(userId)
                        .message(team.getTeamName()+"팀에서 가입을 수락했습니다.")
                        .notificationType(NotificationType.APPROVE)
                        .targetId(teamId)
                        .build()
        );

        teamUserRepository.save(
                TeamUser.builder()
                        .userId(userId)
                        .teamId(teamId)
                        .teamRole(TeamRole.ROLE_MEMBER)
                        .build()
        );

    }

    // 거절
    @Transactional
    public void rejectTeamJoinRequest(Long leaderId, Long userId, Long teamId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("해당 팀은 존재하지 않습니다."));

        validateLeaderAuthority(leaderId, teamId);

        TeamUser teamUser = getLatestTeamUserOrThrow(userId, teamId, "대기 중인 내역이 없습니다.");

        if(teamUser.getTeamRole() != TeamRole.ROLE_WAITING){
            throw new TeamApplicationException("거절할 수 없습니다.");
        }

        notificationRepository.save(
                Notification.builder()
                        .userId(userId)
                        .message(team.getTeamName()+"팀에서 가입을 거절했습니다.")
                        .notificationType(NotificationType.REJECT)
                        .targetId(teamId)
                        .build()
        );

        teamUserRepository.save(
                TeamUser.builder()
                        .userId(userId)
                        .teamId(teamId)
                        .teamRole(TeamRole.ROLE_REJECTED)
                        .build()
        );

    }

    // 방출
    @Transactional
    public void kickUserFromTeam(Long leaderId, Long userId, Long teamId){
        validateLeaderAuthority(leaderId, teamId);

        TeamUser teamUser = getLatestTeamUserOrThrow(userId, teamId, "해당 팀 회원이 아닙니다.");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("해당 팀은 존재하지 않습니다."));

        Set<TeamRole> kickableRoles = Set.of(TeamRole.ROLE_MANAGER, TeamRole.ROLE_MEMBER);

        if(!kickableRoles.contains(teamUser.getTeamRole())){
            throw new TeamApplicationException("방출할 수 없습니다.");
        }

        notificationRepository.save(
                Notification.builder()
                        .userId(leaderId)
                        .message(team.getTeamName()+"팀에서 방출 당했습니다.")
                        .notificationType(NotificationType.KICK)
                        .targetId(teamId)
                        .build()
        );

        teamUserRepository.save(
                TeamUser.builder()
                        .userId(userId)
                        .teamId(teamId)
                        .teamRole(TeamRole.ROLE_KICKED)
                        .build()
        );
    }

    // 탈퇴
    @Transactional
    public void leaveTeam(User user, Long teamId){
        TeamUser teamUser = getLatestTeamUserOrThrow(user.getId(), teamId, "해당 팀이 아닙니다.");

        if(teamUser.getTeamRole() == TeamRole.ROLE_LEADER){
            throw new TeamApplicationException("감독을 다른 사람에게 위임한 뒤 탈퇴하여 주세요.");
        }

        Long leaderId = teamUserRepository.findLeaderId(teamId);

        Set<TeamRole> allowedToLeave = Set.of(TeamRole.ROLE_MANAGER, TeamRole.ROLE_MEMBER);

        if(!allowedToLeave.contains(teamUser.getTeamRole())){
            throw new TeamApplicationException("해당 팀이 아닙니다.");
        }

        // 리더에게 특정 회원이 탈퇴했다고 알림 전송
        notificationRepository.save(
                Notification.builder()
                        .userId(leaderId)
                        .message(user.getName()+"("+user.getEmail()+")님께서 탈퇴했습니다.")
                        .notificationType(NotificationType.LEFT)
                        .targetId(teamId)
                        .build()
        );

        teamUserRepository.save(
                TeamUser.builder()
                        .userId(user.getId())
                        .teamId(teamId)
                        .teamRole(TeamRole.ROLE_LEFT)
                        .build()
        );
    }

    // 가입 요청 취소
    @Transactional
    public void cancelJoinRequest(Long userId, Long teamId){
        TeamUser teamUser = getLatestTeamUserOrThrow(userId, teamId, "가입 요청 내역이 없습니다.");

        if (teamUser.getTeamRole() != TeamRole.ROLE_WAITING) {
            throw new TeamApplicationException("현재 상태에서는 요청을 취소할 수 없습니다.");
        }

        teamUserRepository.delete(teamUser);
    }

    // 승인 대기 리스트
    public List<TeamUserSimpleResponse> getWaitingJoinRequests(Long userId, Long teamId){
        validateLeaderAuthority(userId, teamId);
        List<String> waitingRoles = List.of(
                TeamRole.ROLE_WAITING.name()
        );

        return convertToSimpleResponses(teamId, waitingRoles);
    }

    // 회원 리스트
    public List<TeamUserSimpleResponse> getTeamUsers(Long userId, Long teamId){
        List<String> activeRoles = List.of(
                TeamRole.ROLE_LEADER.name(),
                TeamRole.ROLE_MEMBER.name(),
                TeamRole.ROLE_MANAGER.name()
        );
        
        return convertToSimpleResponses(teamId, activeRoles);
    }

    private List<TeamUserSimpleResponse> convertToSimpleResponses(Long teamId, List<String> teamRoles) {
        List<TeamUser> teamUsers = teamUserRepository.findAllByTeamIdAndTeamRoleIn(teamId, teamRoles);
        return teamUsers.stream()
                .map(teamUser -> {
                    User user = authRepository.findById(teamUser.getUserId())
                            .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
                    return TeamUserSimpleResponse.from(user, teamUser);
                })
                .collect(Collectors.toList());
    }

    // 권한이 리더인지 확인
    private void validateLeaderAuthority(Long userId, Long teamId) {
        teamUserRepository.findLatestByUserIdAndTeamId(userId, teamId)
                .filter(tu -> tu.getTeamRole() == TeamRole.ROLE_LEADER)
                .orElseThrow(() -> new ForbiddenException("해당 작업을 수행할 권한이 없습니다."));
    }

    private TeamUser getLatestTeamUserOrThrow(Long userId, Long teamId, String message) {
        return teamUserRepository.findLatestByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new TeamApplicationException(message));
    }
}
