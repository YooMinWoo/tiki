package com.example.tiki.notion.service;

import com.example.tiki.auth.domain.User;
import com.example.tiki.notion.dto.CreateNotionRequest;
import com.example.tiki.notion.dto.NotionDetailDto;
import com.example.tiki.notion.dto.NotionListDto;
import com.example.tiki.notion.dto.SearchNotionCondition;

import java.util.List;

public interface NotionService {

    // 공지사항 등록
    void createNotion(User user, CreateNotionRequest request);

    // 공지사항 리스트 조회
    List<NotionListDto> getNotionList(SearchNotionCondition condition);

    // 공지사항 상세 조회
    NotionDetailDto getNotionDetail(Long notionId);

    // 공지사항 수정
    void updateNotion(CreateNotionRequest request, Long notionId);

    // 공지사항 삭제
    void deleteNotion(Long notionId);
}
