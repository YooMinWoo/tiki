package com.example.tiki.notice.repository;

import com.example.tiki.notice.domain.entity.Notice;
import com.example.tiki.notice.dto.SearchNoticeCondition;

import java.util.List;

public interface NoticeRepositoryCustom {
    List<Notice> searchNotionList(SearchNoticeCondition condition);
}
