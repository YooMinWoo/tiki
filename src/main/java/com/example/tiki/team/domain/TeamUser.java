package com.example.tiki.team.domain;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TeamUser extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "team_user_id")
    private Long id;

    private Long userId;
    private Long teamId;

    @Enumerated(EnumType.STRING)
    private TeamUserRole teamUserRole;

    @Enumerated(EnumType.STRING)
    private TeamUserStatus teamUserStatus;

    private LocalDateTime joinedAt;

    // 상태 변경
    public void changeStatus(TeamUserStatus teamUserStatus){
        switch (teamUserStatus){
            case APPROVED ->  joinedAt = LocalDateTime.now();
        }
        this.teamUserStatus = teamUserStatus;
    }

}
