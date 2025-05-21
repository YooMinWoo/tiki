package com.example.tiki.notifircation.domain;

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
public class Notification {

    @Id
    @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    private Long userId;

    private String message;
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    private Long targetId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    public void markAsRead(){
        isRead = true;
    }
}
