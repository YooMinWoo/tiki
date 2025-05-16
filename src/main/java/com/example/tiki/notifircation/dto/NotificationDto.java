package com.example.tiki.notifircation.dto;

import com.example.tiki.notifircation.domain.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private Long notificationId;

    private String message;
    private boolean isRead;
    private NotificationType notificationType;
    private Long targetId;
    private String redirectUrl;
    private LocalDateTime createdDate;

}
