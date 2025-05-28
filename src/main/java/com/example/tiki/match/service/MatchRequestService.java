package com.example.tiki.match.service;

import com.example.tiki.match.dto.DecideStatus;
import com.example.tiki.match.dto.MatchRequestResponse;

import java.util.List;

public interface MatchRequestService {

    // 매칭 신청
    void applyForMatch(Long userId, Long teamId, Long matchPostId);

    // 매칭 수락/거절
    void decideMatchRequest(Long userId, Long matchRequestId, DecideStatus decideStatus);

    // 매칭 내역 조회
    List<MatchRequestResponse> getMatchRequestList(Long teamId);
}
