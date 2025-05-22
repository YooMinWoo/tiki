package com.example.tiki.recruitment.service;

import com.example.tiki.recruitment.dto.RecruitmentCreateRequest;
import com.example.tiki.recruitment.dto.RecruitmentUpdateRequest;

public interface RecruitmentService {
    // 모집글 등록
    void createRecruitmentPost(Long userId, Long teamId, RecruitmentCreateRequest recruitmentCreateRequest);

    // 모집글 수정
    void updateRecruitmentPost(Long userId, RecruitmentUpdateRequest request);

    // 모집글 마감
    void closeRecruitmentPost(Long userId, Long recruitmentId);

    // 모집글 삭제
    void deleteRecruitmentPost(Long userId, Long recruitmentId);

    // 특정 id 모집글 조회

    // 모집글 조회
}
