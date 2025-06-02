package com.example.tiki.notice;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.notice.domain.entity.Notice;
import com.example.tiki.notice.dto.CreateNoticeRequest;
import com.example.tiki.notice.repository.NoticeRepository;
import com.example.tiki.notice.service.NoticeService;
import com.example.tiki.team.domain.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class NoticeCreateTest {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeService noticeService;

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
        CreateNoticeRequest request = CreateNoticeRequest.builder()
                                        .title("공지사항 테스트 타이틀")
                                        .content("공지사항 테스트 내용")
                                        .build();
        noticeService.createNotion(admin, request);

        Notice notice = noticeRepository.findAll().get(0);
        assertThat(notice.getTitle()).isEqualTo(request.getTitle());
        assertThat(notice.getContent()).isEqualTo(request.getContent());
        assertThat(notice.getUserId()).isEqualTo(admin.getId());
    }


}
