package com.example.tiki.match.dto;

import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.team.domain.entity.Team;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MatchPostMatchedResponse {
    // hostTeamId, hostTeamName, applicantTeamId, applicantTeamName, title, status, startTime, endTime
    private Long hostTeamId;
    private String hostTeamName;

    private Long applicantTeamId;
    private String applicantTeamName;

    private String title;

    private MatchStatus matchStatus;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static MatchPostMatchedResponse create(MatchPost matchPost, Team hostTeam, Team applicantTeam){
        return MatchPostMatchedResponse.builder()
                    .hostTeamId(hostTeam.getId())
                    .hostTeamName(hostTeam.getTeamName())
                    .applicantTeamId(applicantTeam.getId())
                    .applicantTeamName(applicantTeam.getTeamName())
                    .title(matchPost.getTitle())
                    .matchStatus(matchPost.getMatchStatus())
                    .startTime(matchPost.getStartTime())
                    .endTime(matchPost.getEndTime())
                    .build();
    }
}
