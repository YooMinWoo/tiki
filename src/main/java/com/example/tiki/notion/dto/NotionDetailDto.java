package com.example.tiki.notion.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotionDetailDto {
    private Long notionId;
    private Long writerId;
    private String writerName;

    private String title;
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;

}
