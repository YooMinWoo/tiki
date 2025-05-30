package com.example.tiki.notion.dto;

import com.example.tiki.notion.domain.enums.NotionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchNotionCondition {
    private String keyword;
    private NotionSortType sortType;
}
