package com.example.tiki.team.domain;

import com.example.tiki.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Team extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String teamName;
    private String teamDescription;

    @Enumerated(EnumType.STRING)
    private TeamStatus teamStatus;
}
