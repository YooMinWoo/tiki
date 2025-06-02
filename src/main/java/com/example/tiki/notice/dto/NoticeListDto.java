package com.example.tiki.notice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NoticeListDto {

    private Long notionId;
    private String title;
    private LocalDateTime createDate;
}
