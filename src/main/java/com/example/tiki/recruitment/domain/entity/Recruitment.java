package com.example.tiki.recruitment.domain.entity;

import com.example.tiki.global.entity.BaseEntity;
import com.example.tiki.recruitment.domain.enums.RecruitmentStatus;
import com.example.tiki.recruitment.dto.RecruitmentCreateRequest;
import com.example.tiki.recruitment.dto.RecruitmentUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Recruitment extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "recruitment_id")
    private Long id;

    private Long teamId;

    private String title;

    private String content;

//    @Enumerated(EnumType.STRING)
//    private RecruitmentType recruitmentType; // MEMBER or MERCENARY

    @Enumerated(EnumType.STRING)
    private RecruitmentStatus recruitmentStatus; // OPEN, CLOSED, DELETED

    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    public static Recruitment create(Long teamId ,RecruitmentCreateRequest recruitmentCreateRequest){
        return Recruitment.builder()
                .teamId(teamId)
                .title(recruitmentCreateRequest.getTitle())
                .content(recruitmentCreateRequest.getContent())
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .build();
    }

    public void update(RecruitmentUpdateRequest request){
        this.title = request.getTitle();
        this.content = request.getContent();
    }

    public void closed(){
        this.recruitmentStatus = RecruitmentStatus.CLOSE;
        this.closedAt = LocalDateTime.now();
    }

    public void reopen(){
        this.recruitmentStatus = RecruitmentStatus.OPEN;
        this.openedAt = LocalDateTime.now();
        this.closedAt = null;
    }

    public void deleted(){
        this.recruitmentStatus = RecruitmentStatus.DELETED;
    }

}
