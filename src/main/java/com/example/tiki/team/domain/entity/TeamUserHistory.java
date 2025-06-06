package com.example.tiki.team.domain.entity;

import com.example.tiki.global.entity.BaseEntity;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class TeamUserHistory extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "team_user_history_id")
    private Long id;

    private Long teamUserId;

    private Long userId;
    private Long teamId;

    @Enumerated(EnumType.STRING)
    private TeamUserRole previousRole;      // 변경 전 역할

    @Enumerated(EnumType.STRING)
    private TeamUserRole currentRole;       // 변경 후 역할

    @Enumerated(EnumType.STRING)
    private TeamUserStatus previousStatus;  // 변경 전 상태

    @Enumerated(EnumType.STRING)
    private TeamUserStatus currentStatus;   // 변경 후 상태
}
