package com.example.tiki.match.dto;

import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.entity.MatchRequest;
import com.example.tiki.match.domain.enums.RequestStatus;
import com.example.tiki.team.domain.entity.Team;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MatchRequestResponse {
    // start, end, 글 제목, 글 id, 상태, 상대팀 id, 상대팀 이름
    private Long matchPostId;
    private String title;

    private Long hostTeamId;
    private String hostTeamName;

    private RequestStatus requestStatus;

    private LocalDateTime startTime;  // 경기 시작 시간
    private LocalDateTime endTime;    // 경기 종료 시간

    public static MatchRequestResponse create(MatchPost matchPost, Team hostTeam, MatchRequest matchRequest){
        return MatchRequestResponse.builder()
                    .matchPostId(matchPost.getId())
                    .title(matchPost.getTitle())
                    .hostTeamId(hostTeam.getId())
                    .hostTeamName(hostTeam.getTeamName())
                    .requestStatus(matchRequest.getRequestStatus())
                    .startTime(matchPost.getStartTime())
                    .endTime(matchPost.getEndTime())
                    .build();

    }

}
