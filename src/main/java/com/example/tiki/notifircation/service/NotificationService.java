package com.example.tiki.notifircation.service;

import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.dto.NotificationDto;
import com.example.tiki.notifircation.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 모든 알림 가져오기
    public List<NotificationDto> getNotification(Long userId){
        return notificationRepository.findByUserId(userId).stream()
                .map(notification -> NotificationDto.builder()
                        .notificationId(notification.getId())
                        .message(notification.getMessage())
                        .isRead(notification.isRead())
                        .targetId(notification.getTargetId())
                        .notificationType(notification.getNotificationType())
                        .redirectUrl(getRedirectUrl(notification.getNotificationType(), notification.getTargetId()))
                        .createdDate(notification.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

    // 모두 읽음 처리
    @Transactional
    public void markAllAsRead(Long userId){
        notificationRepository.findByUserId(userId).forEach(Notification::markAsRead);
    }

    // 단건 읽음 처리
    @Transactional
    public void markAsRead(Long userId, Long notificationId){
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("해당 알림을 찾을 수 없습니다."));
        if(notification.getUserId() != userId) throw new ForbiddenException("알림 읽음처리는 본인만 할 수 있습니다.");

        notification.markAsRead();
    }

    public String getRedirectUrl(NotificationType type, Long targetId){

        return switch (type) {
            case MATCHPOST -> "/api/matches/" + targetId;
            case MATCHREQUEST -> "/api/matches/" + targetId;
            case RECRUIT -> "/api/recruits/" + targetId;
            case FOLLOW, LEFT -> "/api/users/" + targetId;
            case APPROVE, REJECT, KICK -> "/api/teams/" + targetId;
            case JOIN -> "/api/teams/"+targetId+"/join-request/waiting";
            case DISBAND -> "/api/users/teams";
            case NOTHING -> null;
        };
    }
}
