package com.example.tiki.recruitment.service;

import com.example.tiki.auth.domain.User;
import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import com.example.tiki.recruitment.dto.*;

import java.util.List;

public interface RecruitmentService {
    // 모집글 등록
    void createRecruitmentPost(Long userId, Long teamId, RecruitmentCreateRequest recruitmentCreateRequest);

    // 모집글 수정
    void updateRecruitmentPost(Long userId, RecruitmentUpdateRequest request);

    // 모집글 마감
    void closeRecruitmentPost(Long userId, Long recruitmentId);

    // 모집글 삭제
    void deleteRecruitmentPost(User user, Long recruitmentId);

    // 모집글 리오픈
    void reopenRecruitmentPost(Long userId, Long recruitmentId);

    // 모집글 조회(키워드, 상태 필터)
    List<RecruitmentSearchResultDto> getRecruitmentSearchResult(String keyword, RecruitmentStatusVisible status);

    // 모집글 상세 조회
    RecruitmentDetailDto getRecruitmentDetail(Long recruitmentId);
}
