package com.example.tiki.match.domain.entity;

import com.example.tiki.global.entity.BaseEntity;
import com.example.tiki.match.domain.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.apache.coyote.Request;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MatchRequest extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "matchRequest_id")
    private Long id;

    private Long matchPostId;
    private Long applicantTeamId;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    public void changeStatus(RequestStatus requestStatus){
        this.requestStatus = requestStatus;
    }
}
