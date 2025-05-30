package com.example.tiki.notion;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.follow.domain.Follow;
import com.example.tiki.follow.repository.FollowRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.notifircation.domain.Notification;
import com.example.tiki.notifircation.domain.NotificationType;
import com.example.tiki.notifircation.repository.NotificationRepository;
import com.example.tiki.notion.domain.entity.Notion;
import com.example.tiki.notion.dto.CreateNotionRequest;
import com.example.tiki.notion.repository.NotionRepository;
import com.example.tiki.notion.service.NotionService;
import com.example.tiki.recruitment.domain.entity.Recruitment;
import com.example.tiki.recruitment.dto.RecruitmentCreateRequest;
import com.example.tiki.recruitment.repository.RecruitmentRepository;
import com.example.tiki.recruitment.service.RecruitmentService;
import com.example.tiki.team.domain.entity.Team;
import com.example.tiki.team.domain.entity.TeamUser;
import com.example.tiki.team.domain.enums.TeamStatus;
import com.example.tiki.team.domain.enums.TeamUserRole;
import com.example.tiki.team.domain.enums.TeamUserStatus;
import com.example.tiki.team.repository.TeamRepository;
import com.example.tiki.team.repository.TeamUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class NotionCreateTest {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private NotionRepository notionRepository;

    @Autowired
    private NotionService notionService;

    private User admin;
    private User member;
    private Team team;

    @BeforeEach
    void 더미데이터_생성(){
        admin = authRepository.save(
                User.builder()
                        .name("admin")
                        .email("admin@naver.com")
                        .role(Role.ROLE_ADMIN)
                        .build());

        member = authRepository.save(
                User.builder()
                        .name("member")
                        .email("member@naver.com")
                        .role(Role.ROLE_USER)
                        .build());

    }

    @Test
    void 공지사항_생성_성공(){
        CreateNotionRequest request = CreateNotionRequest.builder()
                                        .title("공지사항 테스트 타이틀")
                                        .content("공지사항 테스트 내용")
                                        .build();
        notionService.createNotion(admin, request);

        Notion notion = notionRepository.findAll().get(0);
        assertThat(notion.getTitle()).isEqualTo(request.getTitle());
        assertThat(notion.getContent()).isEqualTo(request.getContent());
        assertThat(notion.getUserId()).isEqualTo(admin.getId());
    }


}
