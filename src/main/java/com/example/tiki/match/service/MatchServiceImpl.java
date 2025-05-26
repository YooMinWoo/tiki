package com.example.tiki.match.service;

import com.example.tiki.api.kakao.GeoCoordinate;
import com.example.tiki.api.kakao.KakaoMapService;
import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.dto.MatchPostSearchResponse;
import com.example.tiki.match.dto.MatchPostRequest;
import com.example.tiki.match.dto.MatchPostSearchCondition;
import com.example.tiki.match.repository.MatchPostRepository;
import com.example.tiki.match.repository.MatchRequestRepository;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.utils.CheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final CheckUtil checkUtil;
    private final KakaoMapService kakaoMapService;
    private final MatchPostRepository matchPostRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;

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

    private String getFullAddress(MatchPostRequest request){
        String region = request.getRegion();
        String city = request.getCity();
        String roadName = request.getRoadName();
        String buildingNumber = request.getBuildingNumber();

        return region + " " + city + " " + roadName + " " + buildingNumber;
    }
}
