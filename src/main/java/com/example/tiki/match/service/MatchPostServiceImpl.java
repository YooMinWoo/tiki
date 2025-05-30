package com.example.tiki.match.service;

import com.example.tiki.api.kakao.GeoCoordinate;
import com.example.tiki.api.kakao.KakaoMapService;
import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.entity.MatchRequest;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.domain.enums.RequestStatus;
import com.example.tiki.match.dto.*;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchPostServiceImpl implements MatchPostService {

    private final CheckUtil checkUtil;
    private final KakaoMapService kakaoMapService;
    private final MatchPostRepository matchPostRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;
    private final TeamUserRepository teamUserRepository;

    // 매칭글 생성
    @Override
    @Transactional
    public void createMatchPost(Long userId, MatchPostRequest request) {
        // 팀이 존재하는지 확인
        Team team = checkUtil.validateAndGetTeam(request.getHostTeamId());

        // 팀이 활성화 상태인지 확인
        if(team.getTeamStatus() == TeamStatus.INACTIVE) throw new IllegalStateException("비활성화 상태입니다.");

        // 수행하려는 주체의 권한이 리더인지 확인
        checkUtil.validateLeaderAuthority(userId, team.getId());

        // 시작시간 < 종료시간
        if(!request.getStartTime().isBefore(request.getEndTime())) throw new IllegalArgumentException("종료시간은 시작시간과 같거나 빠를 수 없습니다.");

        // 카카오 맵 API 호출(위도,경도 가져오기)
        GeoCoordinate geoCoordinate = kakaoMapService.getCoordinates(getFullAddress(request)).block();

        // 위도 추출
        Double latitude = geoCoordinate.getLatitude();

        // 경도 추출
        Double longitude = geoCoordinate.getLongitude();

        // 매칭글 생성
        MatchPost matchPost = MatchPost.create(request, latitude, longitude);
        matchPostRepository.save(matchPost);

        // 팔로워에게 알림 전송
        List<Follow> follows = followRepository.findByTeamId(request.getHostTeamId());
        for (Follow follow : follows) {
            notificationRepository.save(Notification.builder()
                    .userId(follow.getUserId())
                    .message(team.getTeamName() + "팀이 매칭글을 올렸습니다.")
                    .notificationType(NotificationType.MATCHPOST)
                    .targetId(matchPost.getId())
                    .build());
        }
    }

    // 매칭 취소 (매칭글에서 취소로 변환 -> request 또한 취소로 변환)
    @Transactional
    public void cancelMatch(Long userId, Long matchPostId) {
        // 모집자가 취소하는 경우, 신청자가 취소하는 경우

        // 매칭 글 조회하기
        MatchPost matchPost = checkUtil.validateAndGetMatchPost(matchPostId);

        // 매칭 글의 상태가 MATCHED인지 확인
        if(matchPost.getMatchStatus() != MatchStatus.MATCHED) throw new IllegalStateException("매칭이 성사된 상태가 아닙니다.");

        // 매칭 성사된 request 조회
        MatchRequest matchRequest = matchRequestRepository.findByMatchPostIdAndRequestStatus(matchPost.getId(), RequestStatus.ACCEPTED)
                        .orElseThrow(() -> new NotFoundException("에러가 발생하였습니다."));

        // 매칭 글 올린 팀
        Team hostTeam = checkUtil.validateAndGetTeam(matchPost.getHostTeamId());

        // 매칭 신청한 팀
        Team applicantTeam = checkUtil.validateAndGetTeam(matchPost.getApplicantTeamId());

        TeamUser hostTeamLeader = teamUserRepository.findByTeamIdAndTeamUserRole(hostTeam.getId(), TeamUserRole.ROLE_LEADER);
        TeamUser applicantTeamLeader = teamUserRepository.findByTeamIdAndTeamUserRole(applicantTeam.getId(), TeamUserRole.ROLE_LEADER);

        // 취소자가 매칭 글 올린 팀인 경우
        boolean isHost = hostTeamLeader.getUserId().equals(userId);

        // 취소자가 매칭 신청한 팀인 경우
        boolean isApplicant = applicantTeamLeader.getUserId().equals(userId);

        if(!isHost && !isApplicant) throw new ForbiddenException("해당 작업을 수행할 권한이 없습니다.");

        // 글 상태 변경 / 요청 상태 변경
        matchRequest.changeStatus(RequestStatus.CANCELED);
        matchPost.changeStatus(MatchStatus.CANCELED);

        Long receiverUserId;
        String message;

        if (isHost) {
            receiverUserId = applicantTeamLeader.getUserId();
            message = hostTeam.getTeamName() + "에서 매칭을 취소했습니다.";
        } else {
            receiverUserId = hostTeamLeader.getUserId();
            message = applicantTeam.getTeamName() + "에서 매칭을 취소했습니다.";
        }

        Notification notification = Notification.builder()
                .userId(receiverUserId)
                .notificationType(NotificationType.MATCHPOST)
                .targetId(matchPostId)
                .message(message)
                .build();

        notificationRepository.save(notification);

    }

    // 매칭글 조회
    @Override
    public List<MatchPostSearchResponse> searchMatchPost(MatchPostSearchCondition condition) {
        List<MatchPostSearchResponse> result = new ArrayList<>();
        List<MatchPost> matchPosts = matchPostRepository.search(condition);
        for (MatchPost matchPost : matchPosts) {
            Team hostTeam = checkUtil.validateAndGetTeam(matchPost.getHostTeamId());
            result.add(MatchPostSearchResponse.from(matchPost, hostTeam.getTeamName()));
        }
        return result;
    }

    // 매칭글 수정
    @Override
    @Transactional
    public void updateMatchPost(Long userId, Long matchPostId, MatchPostRequest request) {
        // 존재하는 매칭글인지 확인
        MatchPost matchPost = checkUtil.validateAndGetMatchPost(matchPostId);

        // 팀이 일치하는지 확인
        if(request.getHostTeamId() != matchPost.getHostTeamId()) throw new ForbiddenException("접근 권한이 없습니다.");

        // 팀 entity 가져오기
        Team team = checkUtil.validateAndGetTeam(matchPost.getHostTeamId());

        // 매칭글이 활성화 상태인지 확인 (OPEN이 아닐 경우 수정 불가능)
        if(matchPost.getMatchStatus() != MatchStatus.OPEN) throw new IllegalStateException("수정이 불가능한 상태입니다.");

        // 수행하려는 주체의 권한이 리더인지 확인
        checkUtil.validateLeaderAuthority(userId, team.getId());

        // 시작시간 < 종료시간
        if(!request.getStartTime().isBefore(request.getEndTime())) throw new IllegalArgumentException("종료시간은 시작시간과 같거나 빠를 수 없습니다.");

        Double latitude = null;
        Double longitude = null;

        // 주소가 변경되었으면 update
        if(!matchPost.getFullAddress().equals(getFullAddress(request))){
            // 카카오 맵 API 호출(위도,경도 가져오기)
            GeoCoordinate geoCoordinate = kakaoMapService.getCoordinates(getFullAddress(request)).block();

            // 위도 추출
            latitude = geoCoordinate.getLatitude();

            // 경도 추출
            longitude = geoCoordinate.getLongitude();
        }
        matchPost.update(request, latitude, longitude);
    }


    // 매칭글 삭제
    @Override
    @Transactional
    public void deleteMatchPost(User user, Long matchPostId) {
        // 존재하는 매칭글인지 확인
        MatchPost matchPost = checkUtil.validateAndGetMatchPost(matchPostId);

        // 팀 entity 가져오기
        Team team = checkUtil.validateAndGetTeam(matchPost.getHostTeamId());

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        // 수행하려는 주체의 권한이 리더인지 확인
        if(!isAdmin) checkUtil.validateLeaderAuthority(user.getId(), team.getId());

        // 관리자의 경우 삭제 및 알림 발송
        if(isAdmin){
            // 신청자의 경우 신청 상태도 변경한다.
            Long hostTeamId = matchPost.getHostTeamId();
            Long applicantTeamId = matchPost.getApplicantTeamId();

            TeamUser hostTeamLeader = teamUserRepository.findByTeamIdAndTeamUserRole(hostTeamId, TeamUserRole.ROLE_LEADER);
            TeamUser applicantTeamLeader = teamUserRepository.findByTeamIdAndTeamUserRole(applicantTeamId, TeamUserRole.ROLE_LEADER);

            MatchRequest matchRequest = matchRequestRepository.findByMatchPostIdAndApplicantTeamId(matchPostId, applicantTeamId)
                    .orElseThrow(() -> new IllegalArgumentException("삭제 도중 에러가 발생했습니다."));

            matchRequest.changeStatus(RequestStatus.CANCELED_BY_ADMIN);

            // 매칭 글 올린 팀에게 알림 발송
            notificationRepository.save(
                    Notification.builder()
                            .userId(hostTeamLeader.getUserId())
                            .message("관리자에 의해 글이 삭제 및 매칭이 취소되었습니다.")
                            .notificationType(NotificationType.MATCHPOSTLIST)
                            .targetId(hostTeamLeader.getTeamId())
                            .build()
            );

            // 매칭 신청한 팀에게 알림 발송
            notificationRepository.save(
                    Notification.builder()
                            .userId(applicantTeamLeader.getUserId())
                            .message("관리자에 의해 글이 삭제 및 매칭이 취소되었습니다.")
                            .notificationType(NotificationType.MATCHPOSTLIST)
                            .targetId(applicantTeamLeader.getTeamId())
                            .build()
            );

            matchPost.changeStatus(MatchStatus.DELETED_BY_ADMIN);
            return;

            // 매칭 성사 완료 상태인지 확인 (MATCHED일 경우 삭제 불가능)
        } else if(matchPost.getMatchStatus() != MatchStatus.OPEN) throw new IllegalStateException("삭제가 불가능합니다.");

        matchPost.changeStatus(MatchStatus.DELETED);
    }

    // 팀 별 매칭글 내역 조회
    @Override
    public List<MatchPostSearchResponse> searchMatchPostByTeam(Long teamId, MatchPostByTeamSearchCondition condition) {
        List<MatchPostSearchResponse> result = new ArrayList<>();
        List<MatchPost> matchPosts = matchPostRepository.searchByTeam(teamId, condition);
        for (MatchPost matchPost : matchPosts) {
            Team hostTeam = checkUtil.validateAndGetTeam(matchPost.getHostTeamId());
            result.add(MatchPostSearchResponse.from(matchPost, hostTeam.getTeamName()));
        }
        return result;
    }

    // 매칭글 상세페이지 조회
    @Override
    public MatchPostResponse getMatchPostDetail(Long matchPostId) {
        // 존재하는 매칭글인지 확인
        MatchPost matchPost = checkUtil.validateAndGetMatchPost(matchPostId);

        // 팀 entity 가져오기
        Team hostTeam = checkUtil.validateAndGetTeam(matchPost.getHostTeamId());
        Team applicantTeam = null;

        // 상대팀 entity 가져오기
        if(matchPost.getApplicantTeamId() != null){
            applicantTeam = checkUtil.validateAndGetTeam(matchPost.getApplicantTeamId());
        }

        return MatchPostResponse.create(matchPost, hostTeam, applicantTeam);
    }

    // 매칭 일정
    @Override
    public List<MatchPostMatchedResponse> getMatched(Long teamId){
        List<MatchPostMatchedResponse> result = new ArrayList<>();
        List<MatchPost> matchPosts = matchPostRepository.searchMatched(teamId);
        for (MatchPost matchPost : matchPosts) {
            Team hostTeam = checkUtil.validateAndGetTeam(matchPost.getHostTeamId());
            Team applicantTeam = checkUtil.validateAndGetTeam(matchPost.getApplicantTeamId());
            result.add(MatchPostMatchedResponse.create(matchPost, hostTeam, applicantTeam));
        }
        return result;
    }

    // 특정 매칭 글에 대한 매칭 요청 리스트
    @Override
    public List<MatchRequestsForPost> getMatchRequestsForPost(Long matchPostId) {
        List<MatchRequestsForPost> result = new ArrayList<>();
        MatchPost matchPost = checkUtil.validateAndGetMatchPost(matchPostId);
        List<MatchRequest> matchRequestList = matchRequestRepository.findByMatchPostId(matchPost.getId());
        for (MatchRequest matchRequest : matchRequestList) {
            Team applicantTeam = checkUtil.validateAndGetTeam(matchRequest.getApplicantTeamId());
            result.add(MatchRequestsForPost.from(matchPost, matchRequest, applicantTeam));
        }
        return result;
    }



    private String getFullAddress(MatchPostRequest request){
        String region = request.getRegion();
        String city = request.getCity();
        String roadName = request.getRoadName();
        String buildingNumber = request.getBuildingNumber();

        return region + " " + city + " " + roadName + " " + buildingNumber;
    }
}
