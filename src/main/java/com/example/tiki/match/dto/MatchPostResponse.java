package com.example.tiki.match.dto;

import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.team.domain.entity.Team;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class MatchPostResponse {

    private Long hostTeamId;        // 게시글 올린 팀 ID (주최)
    private Long applicantTeamId;   // 매칭 신청 승낙된 팀 ID (참여)

    private String hostTeamName;	// 게시글 올린 팀 이름 (주최)
    private String applicantTeamName;   // 매칭 신청 승낙된 팀 이름 (참여)

    private String title;
    private String content;

    private LocalDateTime startTime;  // 경기 시작 시각 (예: 08:00)
    private LocalDateTime endTime;    // 경기 종료 시각 (예: 10:00)

    private String region;          // 시/도
    private String city;            // 시/군/구
    private String roadName;        // 도로명
    private String buildingNumber;  // 건물번호
    private String detailAddress;   // 상세주소

    private Double latitude;        // 위도
    private Double longitude;       // 경도

    private MatchStatus matchStatus;

    public static MatchPostResponse create(MatchPost matchPost, Team hostTeam, Team applicantTeam){
        MatchPostResponseBuilder builder = MatchPostResponse.builder()
                .hostTeamId(hostTeam.getId())
                .hostTeamName(hostTeam.getTeamName())
                .title(matchPost.getTitle())
                .content(matchPost.getContent())
                .startTime(matchPost.getStartTime())
                .endTime(matchPost.getEndTime())
                .region(matchPost.getRegion())
                .city(matchPost.getCity())
                .roadName(matchPost.getRoadName())
                .buildingNumber(matchPost.getBuildingNumber())
                .detailAddress(matchPost.getDetailAddress())
                .latitude(matchPost.getLatitude())
                .longitude(matchPost.getLongitude())
                .matchStatus(matchPost.getMatchStatus());
        if(applicantTeam != null){
            builder
                    .applicantTeamId(applicantTeam.getId())
                    .applicantTeamName(applicantTeam.getTeamName());
        }
        return builder.build();
    }
}
