package com.example.tiki.team.domain.entity;

import com.example.tiki.global.entity.BaseEntity;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import jakarta.persistence.*;
import lombok.*;

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
            case APPROVED ->  {
                this.joinedAt = LocalDateTime.now();
                this.teamUserRole = TeamUserRole.ROLE_MEMBER;
            }
        }
        this.teamUserStatus = teamUserStatus;
    }

    // 권한 변경
    public void changeRole(TeamUserRole teamUserRole){
        this.teamUserRole = teamUserRole;
    }

}
