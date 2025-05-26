package com.example.tiki.match.service;


import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.dto.MatchPostRequest;
import com.example.tiki.match.dto.MatchPostResponse;
import com.example.tiki.match.dto.MatchPostSearchCondition;
import com.example.tiki.match.dto.MatchPostSearchResponse;

import java.util.List;

public interface MatchService {

    void createMatchPost(Long userId, MatchPostRequest request);
    List<MatchPostSearchResponse> searchMatchPost(MatchPostSearchCondition condition);
}
