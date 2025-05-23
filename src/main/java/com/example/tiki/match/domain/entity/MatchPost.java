package com.example.tiki.match.domain.entity;

import com.example.tiki.global.entity.BaseEntity;
import com.example.tiki.match.domain.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
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

    private LocalDate matchDate;  // 경기 날짜
    private LocalTime startTime;  // 경기 시작 시각 (예: 08:00)
    private LocalTime endTime;    // 경기 종료 시각 (예: 10:00)

    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;

    private String region;          // 시/도
    private String city;            // 시/군/구
    private String roadName;        // 도로명
    private String buildingNumber;  // 건물번호
    private String detailAddress;   // 상세주소

    private Double latitude;        // 위도
    private Double longitude;       // 경도

}
