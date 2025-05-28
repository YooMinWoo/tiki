package com.example.tiki.match.domain.entity;

import com.example.tiki.global.entity.BaseEntity;
import com.example.tiki.match.domain.enums.MatchStatus;
import com.example.tiki.match.dto.MatchPostRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MatchPost extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "matchPost_id")
    private Long id;

    private Long hostTeamId;        // 게시글 올린 팀 (주최)
    private Long applicantTeamId;   // 매칭 신청 승낙된 팀 (참여)

    private String title;
    private String content;

    private LocalDateTime startTime;  // 경기 시작 시간
    private LocalDateTime endTime;    // 경기 종료 시간

    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;

    private String region;          // 시/도
    private String city;            // 시/군/구
    private String roadName;        // 도로명
    private String buildingNumber;  // 건물번호
    private String detailAddress;   // 상세주소

    private Double latitude;        // 위도
    private Double longitude;       // 경도

    public String getFullAddress(){
        return region + " " + city + " " + roadName + " " + buildingNumber;
    }

    public void approveMatch(Long applicantTeamId){
        this.matchStatus = MatchStatus.MATCHED;
        this.applicantTeamId = applicantTeamId;
    }

    public static MatchPost create(MatchPostRequest request, Double latitude, Double longitude){
        return MatchPost.builder()
                .hostTeamId(request.getHostTeamId())
                .title(request.getTitle())
                .content(request.getContent())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .matchStatus(MatchStatus.OPEN)
                .region(request.getRegion())
                .city(request.getCity())
                .roadName(request.getRoadName())
                .buildingNumber(request.getBuildingNumber())
                .detailAddress(request.getDetailAddress())
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    public void update(MatchPostRequest request, Double latitude, Double longitude) {
        title = request.getTitle();
        content = request.getContent();
        startTime = request.getStartTime();
        endTime = request.getEndTime();

        region = request.getRegion();
        city = request.getCity();
        roadName = request.getRoadName();
        buildingNumber = request.getBuildingNumber();
        detailAddress = request.getDetailAddress();

        if(latitude != null) this.latitude = latitude;
        if(longitude != null) this.longitude = longitude;
    }

    public void changeStatus(MatchStatus matchStatus){
        this.matchStatus = matchStatus;
    }
}
