package com.example.tiki.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowerSummaryDto {

    private Long userId;
    private String userName;
    private String userEmail;
}
