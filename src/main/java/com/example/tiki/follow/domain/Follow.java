package com.example.tiki.follow.domain;

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
@EntityListeners(AuditingEntityListener.class)
public class Follow {

    @Id
    @GeneratedValue
    @Column(name = "follow_id")
    private Long id;

    private Long userId;
    private Long teamId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

}
