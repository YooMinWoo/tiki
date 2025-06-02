package com.example.tiki.notice.service;

import com.example.tiki.auth.domain.User;
import com.example.tiki.notice.dto.CreateNoticeRequest;
import com.example.tiki.notice.dto.NoticeDetailDto;
import com.example.tiki.notice.dto.NoticeListDto;
import com.example.tiki.notice.dto.SearchNoticeCondition;

import java.util.List;

public interface NoticeService {

    // 공지사항 등록
    void createNotion(User user, CreateNoticeRequest request);

    // 공지사항 리스트 조회
    List<NoticeListDto> getNotionList(SearchNoticeCondition condition);

    // 공지사항 상세 조회
    NoticeDetailDto getNotionDetail(Long notionId);

    // 공지사항 수정
    void updateNotion(CreateNoticeRequest request, Long notionId);

    // 공지사항 삭제
    void deleteNotion(Long notionId);
}
