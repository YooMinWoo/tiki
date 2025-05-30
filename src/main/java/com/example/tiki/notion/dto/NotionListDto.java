package com.example.tiki.notion.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotionListDto {

    private Long notionId;
    private String title;
    private LocalDateTime createDate;
}
