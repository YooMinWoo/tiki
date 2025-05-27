package com.example.tiki.match.service;

import com.example.tiki.match.dto.DecideStatus;

public interface MatchRequestService {

    // 매칭 신청
    void applyForMatch(Long userId, Long teamId, Long matchPostId);

    // 매칭 취소
    void cancelMatchRequest();

    // 매칭 승인
    void approveMatchRequest(Long userId, Long matchRequestId);

    // 매칭 거절
    void rejectMatchRequest(Long userId, Long matchRequestId);

    // 매칭 수락/거절
    void decideMatchRequest(Long userId, Long matchRequestId, DecideStatus decideStatus);

    // 매칭 조회

}
