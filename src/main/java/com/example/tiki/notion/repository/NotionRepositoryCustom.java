package com.example.tiki.notion.repository;

import com.example.tiki.notion.domain.entity.Notion;
import com.example.tiki.notion.dto.SearchNotionCondition;

import java.util.List;

public interface NotionRepositoryCustom {
    List<Notion> searchNotionList(SearchNotionCondition condition);
}
