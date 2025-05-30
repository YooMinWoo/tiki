package com.example.tiki.global.scheduler;

import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.entity.MatchRequest;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.domain.enums.RequestStatus;
import com.example.tiki.match.repository.MatchPostRepository;
import com.example.tiki.match.repository.MatchRequestRepository;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.utils.CheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final MatchPostRepository matchPostRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final NotificationRepository notificationRepository;
    private final CheckUtil checkUtil;

    // 매 시각 0분, 10분 (예: 10:10, 10:20, 10:30...)
    @Scheduled(cron = "0 0/10 * * * *")
    @Transactional
    public void updateMatchStatusByScheduler() {
        LocalDateTime now = LocalDateTime.now();

        // 1. MATCHED → COMPLETED
        List<MatchPost> matchedPosts = matchPostRepository
                .findByMatchStatusAndEndTimeBefore(MatchStatus.MATCHED, now);
        for (MatchPost matchPost : matchedPosts) {
            matchPost.changeStatus(MatchStatus.COMPLETED);
        }

        // 2. OPEN → UNMATCHED, PENDING -> REJECTED
        List<MatchPost> openPosts = matchPostRepository
                .findByMatchStatusAndEndTimeBefore(MatchStatus.OPEN, now);
        for (MatchPost matchPost : openPosts) {
            matchPost.changeStatus(MatchStatus.UNMATCHED);
            // 매칭 공고 올린 팀
            Team team = checkUtil.validateAndGetTeam(matchPost.getHostTeamId());

            // 현재 pending 상태인 모든 신청을 reject로 상태 변경하기
            List<MatchRequest> matchRequestList = matchRequestRepository.findAllByMatchPostIdAndRequestStatus(matchPost.getId(), RequestStatus.PENDING);

            for (MatchRequest request : matchRequestList) {
                request.changeStatus(RequestStatus.REJECTED);

                // 알림 전송
                notificationRepository.save(Notification.builder()
                        .userId(request.getApplicantTeamId())
                        .notificationType(NotificationType.MATCHPOST)
                        .targetId(matchPost.getId())
                        .message(team.getTeamName() + "에서 매칭을 거절했습니다.")
                        .build()
                );
            }
        }

    }

    // 매 10초마다 실행 (예: 10:10, 10:20, 10:30...)
    @Scheduled(cron = "0/10 * * * * *")
    public void test1() {
        System.out.println("매 10초마다 실행");
        // 작업 실행 로직
    }

}
