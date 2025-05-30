package com.example.tiki.match.service;


import com.example.tiki.auth.domain.User;
import com.example.tiki.match.dto.*;

import java.util.List;

public interface MatchPostService {

    // 매칭글 생성
    void createMatchPost(Long userId, MatchPostRequest request);

    // 매칭 리스트 조회(검색 필터)
    List<MatchPostSearchResponse> searchMatchPost(MatchPostSearchCondition condition);

    // 매칭글 수정
    void updateMatchPost(Long userId, Long matchPostId, MatchPostRequest request);

    // 매칭글 삭제
    void deleteMatchPost(User user, Long matchPostId);

    // 팀 별 매칭글 내역 조회
    List<MatchPostSearchResponse> searchMatchPostByTeam(Long teamId, MatchPostByTeamSearchCondition condition);

    // 매칭글 상세 조회
    MatchPostResponse getMatchPostDetail(Long matchPostId);

    // 매칭 취소
    void cancelMatch(Long userId, Long matchPostId);

    // 매칭 일정
    List<MatchPostMatchedResponse> getMatched(Long teamId);

    // 특정 매칭 글에 대한 매칭 요청 리스트
    List<MatchRequestsForPost> getMatchRequestsForPost(Long matchPostId);
}
