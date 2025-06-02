package com.example.tiki.notice.domain.entity;

import com.example.tiki.global.entity.BaseEntity;
import com.example.tiki.notice.domain.enums.NoticeStatus;
import com.example.tiki.notice.dto.CreateNoticeRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notice extends BaseEntity {

    // id, 타이틀, 내용, 상태, 작성자

    @Id
    @GeneratedValue
    @Column(name = "notion_id")
    private Long id;

    private Long userId;

    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private NoticeStatus noticeStatus;



    public void update(CreateNoticeRequest request){
        title = request.getTitle();
        content = request.getContent();
    }

    public void delete(){
        this.noticeStatus = NoticeStatus.DELETED;
    }
}
