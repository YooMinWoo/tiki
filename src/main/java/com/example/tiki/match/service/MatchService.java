package com.example.tiki.match.service;


import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.dto.MatchPostRequest;
import com.example.tiki.match.dto.MatchPostResponse;
import com.example.tiki.match.dto.MatchPostSearchCondition;
import com.example.tiki.match.dto.MatchPostSearchResponse;

import java.util.List;

public interface MatchService {

    // 매칭글 생성
    void createMatchPost(Long userId, MatchPostRequest request);

    // 매칭 리스트 조회(검색 필터)
    List<MatchPostSearchResponse> searchMatchPost(MatchPostSearchCondition condition);

    // 매칭글 수정
    void updateMatchPost(Long userId, Long matchPostId, MatchPostRequest request);
}
