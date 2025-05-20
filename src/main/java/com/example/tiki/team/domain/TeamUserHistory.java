package com.example.tiki.team.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TeamUserHistory {
    @Id
    @GeneratedValue
    @Column(name = "team_user_history_id")
    private Long id;

    private Long userId;
    private Long teamId;

    private TeamUserRole previousRole;      // 변경 전 역할
    private TeamUserRole currentRole;       // 변경 후 역할

    private TeamUserStatus previousStatus;  // 변경 전 상태
    private TeamUserStatus currentStatus;   // 변경 후 상태
}
