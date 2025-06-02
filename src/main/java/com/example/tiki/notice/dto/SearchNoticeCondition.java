package com.example.tiki.notice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchNoticeCondition {
    private String keyword;
    private NoticeSortType sortType;
}
