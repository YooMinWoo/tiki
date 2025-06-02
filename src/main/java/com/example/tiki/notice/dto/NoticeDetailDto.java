package com.example.tiki.notice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NoticeDetailDto {
    private Long notionId;
    private Long writerId;
    private String writerName;

    private String title;
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;

}
