package com.example.tiki.match.repository;

import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.dto.MatchPostSearchCondition;

import java.util.List;

public interface MatchPostRepositoryCustom {
    List<MatchPost> search(MatchPostSearchCondition condition);
}
