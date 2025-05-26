package com.example.tiki.match.dto;

import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.enums.MatchStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class MatchPostSearchResponse {

    private Long hostTeamId;        // 게시글 올린 팀 ID (주최)

    private String hostTeamName;	// 게시글 올린 팀 이름 (주최)

    private String title;

    private LocalDate matchDate;  // 경기 날짜
    private LocalTime startTime;  // 경기 시작 시각 (예: 08:00)
    private LocalTime endTime;    // 경기 종료 시각 (예: 10:00)

    private String region;          // 시/도

    private MatchStatus matchStatus;    // 매칭글 상태

    public static MatchPostSearchResponse from(MatchPost matchPost, String hostTeamName){
        return MatchPostSearchResponse.builder()
                .hostTeamId(matchPost.getHostTeamId())
                .hostTeamName(hostTeamName)
                .title(matchPost.getTitle())
                .matchDate(matchPost.getMatchDate())
                .startTime(matchPost.getStartTime())
                .endTime(matchPost.getEndTime())
                .region(matchPost.getRegion())
                .matchStatus(matchPost.getMatchStatus())
                .build();
    }
}
