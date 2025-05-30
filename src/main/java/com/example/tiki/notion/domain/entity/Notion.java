package com.example.tiki.notion.domain.entity;

import com.example.tiki.global.entity.BaseEntity;
import com.example.tiki.notion.domain.enums.NotionStatus;
import com.example.tiki.notion.dto.CreateNotionRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notion extends BaseEntity {

    // id, 타이틀, 내용, 상태, 작성자

    @Id
    @GeneratedValue
    @Column(name = "notion_id")
    private Long id;

    private Long userId;

    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private NotionStatus notionStatus;



    public void update(CreateNotionRequest request){
        title = request.getTitle();
        content = request.getContent();
    }

    public void delete(){
        this.notionStatus = NotionStatus.DELETED;
    }
}
