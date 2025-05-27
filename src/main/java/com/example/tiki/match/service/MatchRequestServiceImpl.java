package com.example.tiki.match.service;

import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.entity.MatchRequest;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.domain.enums.RequestStatus;
import com.example.tiki.match.dto.DecideStatus;
import com.example.tiki.match.repository.MatchPostRepository;
import com.example.tiki.match.repository.MatchRequestRepository;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.repository.TeamUserRepository;
import com.example.tiki.utils.CheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchRequestServiceImpl implements MatchRequestService {

    private final CheckUtil checkUtil;
    private final MatchRequestRepository matchRequestRepository;
    private final MatchPostRepository matchPostRepository;
    private final TeamUserRepository teamUserRepository;
    private final NotificationRepository notificationRepository;

    // 매칭 신청
    @Override
    @Transactional
    public void applyForMatch(Long userId, Long teamId, Long matchPostId) {
        // 매칭 글 가져오기
        MatchPost matchPost = checkUtil.validateAndGetMatchPost(matchPostId);

        // 매칭 글의 상태가 OPEN인지 확인
        if(matchPost.getMatchStatus() != MatchStatus.OPEN) throw new IllegalStateException("신청할 수 있는 상태가 아닙니다.");

        // 유효한 팀인지
        Team team = checkUtil.validateAndGetTeam(teamId);

        // 리더인지 확인
        checkUtil.validateLeaderAuthority(userId, teamId);

        // 활성화 팀인지 확인
        if(team.getTeamStatus() != TeamStatus.ACTIVE) throw new IllegalStateException("팀 비활성화를 해제하여 주세요.");

        // 매칭 신청
        MatchRequest matchRequest = matchRequestRepository.save(
                MatchRequest.builder()
                        .matchPostId(matchPostId)
                        .applicantTeamId(teamId)
                        .requestStatus(RequestStatus.PENDING)
                        .build());

        // 알림 전송
        TeamUser teamUser = teamUserRepository.findByTeamIdAndTeamUserRole(matchPost.getHostTeamId(), TeamUserRole.ROLE_LEADER);
        notificationRepository.save(Notification.builder()
                .userId(teamUser.getUserId())
                .message(team.getTeamName() + "에서 매칭 요청을 보냈습니다.")
                .notificationType(NotificationType.MATCHREQUEST)
                .targetId(team.getId())
                .build());

    }

    // 매칭 취소
    @Override
    @Transactional
    public void cancelMatchRequest() {

    }

    // 매칭 승인
    @Override
    @Transactional
    public void approveMatchRequest(Long userId, Long matchRequestId) {
        // 매칭 승인 정보 가져오기
        MatchRequest matchRequest = checkUtil.getPendingMatchRequest(matchRequestId);

        // 매칭 글 조회하기
        MatchPost matchPost = checkUtil.validateAndGetMatchPost(matchRequest.getMatchPostId());

        // 매칭 글의 상태가 OPEN인지 확인
        if(matchPost.getMatchStatus() != MatchStatus.OPEN) throw new IllegalStateException("수락할 수 없습니다.");

        // 리더인지 확인
        checkUtil.validateLeaderAuthority(userId, matchPost.getHostTeamId());

        // 매칭 성공!
        matchPost.approveMatch(matchRequest.getApplicantTeamId());
        matchRequest.changeStatus(RequestStatus.ACCEPTED);

        // 신청한 팀의 리더의 id 값 가져오기
        TeamUser teamUser = teamUserRepository.findByTeamIdAndTeamUserRole(matchRequest.getApplicantTeamId(), TeamUserRole.ROLE_LEADER);

        // 매칭 공고 올린 팀
        Team team = checkUtil.validateAndGetTeam(matchPost.getHostTeamId());

        // 알림 전송
        notificationRepository.save(Notification.builder()
                .userId(teamUser.getUserId())
                .message(team.getTeamName() + "에서 매칭을 수락했습니다.")
                .notificationType(NotificationType.MATCHPOST)
                .targetId(matchPost.getId())
                .build());
    }

    // 매칭 거절
    @Override
    @Transactional
    public void rejectMatchRequest(Long userId, Long matchRequestId) {
        // 매칭 승인 정보 가져오기
        MatchRequest matchRequest = checkUtil.getPendingMatchRequest(matchRequestId);

        // 매칭 글 조회하기
        MatchPost matchPost = checkUtil.validateAndGetMatchPost(matchRequest.getMatchPostId());

        // 매칭 글의 상태가 OPEN인지 확인
        if(matchPost.getMatchStatus() != MatchStatus.OPEN) throw new IllegalStateException("거절할 수 없습니다.");

        // 리더인지 확인
        checkUtil.validateLeaderAuthority(userId, matchPost.getHostTeamId());

        // 매칭 거절!
        matchRequest.changeStatus(RequestStatus.REJECTED);

        // 신청한 팀의 리더의 id 값 가져오기
        TeamUser teamUser = teamUserRepository.findByTeamIdAndTeamUserRole(matchRequest.getApplicantTeamId(), TeamUserRole.ROLE_LEADER);

        // 매칭 공고 올린 팀
        Team team = checkUtil.validateAndGetTeam(matchPost.getHostTeamId());

        // 알림 전송
        notificationRepository.save(Notification.builder()
                .userId(teamUser.getUserId())
                .message(team.getTeamName() + "에서 매칭을 거절했습니다.")
                .notificationType(NotificationType.MATCHPOST)
                .targetId(matchPost.getId())
                .build());
    }

    // 매칭 수락/거절
    @Transactional
    public void decideMatchRequest(Long userId, Long matchRequestId, DecideStatus decideStatus) {
        // 매칭 승인 정보 가져오기
        MatchRequest matchRequest = checkUtil.getPendingMatchRequest(matchRequestId);

        // 매칭 글 조회하기
        MatchPost matchPost = checkUtil.validateAndGetMatchPost(matchRequest.getMatchPostId());

        // 매칭 글의 상태가 OPEN인지 확인
        if(matchPost.getMatchStatus() != MatchStatus.OPEN) throw new IllegalStateException("진행할 수 없습니다.");

        // 리더인지 확인
        checkUtil.validateLeaderAuthority(userId, matchPost.getHostTeamId());

        // 매칭 성공
        if(decideStatus == DecideStatus.ACCEPTED) {
            matchPost.approveMatch(matchRequest.getApplicantTeamId());
        }
        matchRequest.changeStatus(decideStatus.toMatchStatus());

        // 신청한 팀의 리더의 id 값 가져오기
        TeamUser teamUser = teamUserRepository.findByTeamIdAndTeamUserRole(matchRequest.getApplicantTeamId(), TeamUserRole.ROLE_LEADER);

        // 매칭 공고 올린 팀
        Team team = checkUtil.validateAndGetTeam(matchPost.getHostTeamId());

        // 알림 전송
        Notification.NotificationBuilder builder = Notification.builder()
                            .userId(teamUser.getUserId())
                            .notificationType(NotificationType.MATCHPOST)
                            .targetId(matchPost.getId());

        switch (decideStatus){
            case ACCEPTED -> builder.message(team.getTeamName() + "에서 매칭을 수락했습니다.");
            case REJECTED -> builder.message(team.getTeamName() + "에서 매칭을 거절했습니다.");
        }
        notificationRepository.save(builder.build());
    }
}
