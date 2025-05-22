package com.example.tiki.notification;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.follow.service.FollowService;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.dto.NotificationDto;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.notifircation.service.NotificationService;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class NotificationServiceTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @BeforeEach
    void setUp(){
        // 유저, 팀
        for(int i=1; i<=10; i++){
            authRepository.save(
                    User.builder()
                            .name("test"+i)
                            .email("test"+i+"@test.com")
                            .role(Role.ROLE_USER)
                            .build());
        }
        for(int i=1; i<=10; i++){
            teamRepository.save(Team.builder()
                    .teamName("test" + i + " FC")
                    .teamDescription("test" + i +" FC입니다.")
                    .teamStatus(TeamStatus.ACTIVE)
                    .build());
        }

    }

    @Test
    void 모든_알림_가져오기(){
        // given
        for(int i=1; i<=10; i++){
            notificationRepository.save(Notification.builder()
                    .userId(1L)
                    .message("test" + i)
                    .build()
            );
        }
        // when
        List<NotificationDto> notificationDtos = notificationService.getNotification(1L);
        for(NotificationDto dto : notificationDtos){
            System.out.println(dto);
        }

        assertEquals(10, notificationDtos.size());
    }

    @Test
    void 모두_읽음_처리(){
        // given
        for(int i=1; i<=10; i++){
            notificationRepository.save(Notification.builder()
                    .userId(1L)
                    .message("test" + i)
                    .build()
            );
        }

        // when
        System.out.println("읽기 전");
        for(NotificationDto dto : notificationService.getNotification(1L)){
            System.out.println(dto);
        }

        notificationService.markAllAsRead(1L);

        // then
        System.out.println("\n모두 읽음 처리 후");
        for(NotificationDto dto : notificationService.getNotification(1L)){
            System.out.println(dto);
            assertTrue(dto.isRead());
        }
    }

    @Test
    void 단건_읽음_처리(){
        // given
        for(int i=1; i<=10; i++){
            notificationRepository.save(Notification.builder()
                    .userId(1L)
                    .message("test" + i)
                    .build()
            );
        }

        // when
        System.out.println("읽기 전");
        for(NotificationDto dto : notificationService.getNotification(1L)){
            System.out.println(dto);
        }
        notificationService.markAsRead(1L, 2L);

        // then
        System.out.println("\n단건 읽음 처리 후");
        for(NotificationDto dto : notificationService.getNotification(1L)){
            System.out.println(dto);
        }

        assertTrue(notificationRepository.findById(2L).get().isRead());
    }
}
