package com.example.tiki.match.dto;

import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.entity.MatchRequest;
import com.example.tiki.match.domain.enums.RequestStatus;
import com.example.tiki.team.domain.entity.Team;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchRequestsForPost {
    private Long matchPostId;
    private String matchPostTitle;

    private Long matchRequestId;

    private Long applicantTeamId;
    private String applicantTeamName;

    private RequestStatus requestStatus;

    public static MatchRequestsForPost from(MatchPost matchPost, MatchRequest matchRequest, Team applicantTeam){
        return MatchRequestsForPost.builder()
                    .matchPostId(matchPost.getId())
                    .matchPostTitle(matchPost.getTitle())
                    .matchRequestId(matchRequest.getId())
                    .applicantTeamId(applicantTeam.getId())
                    .applicantTeamName(applicantTeam.getTeamName())
                    .requestStatus(matchRequest.getRequestStatus())
                    .build();
    }
}
