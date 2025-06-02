package com.example.tiki.notice;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.notice.domain.entity.Notice;
import com.example.tiki.notice.domain.enums.NoticeStatus;
import com.example.tiki.notice.dto.*;
import com.example.tiki.notice.repository.NoticeRepository;
import com.example.tiki.notice.service.NoticeService;
import com.example.tiki.team.domain.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class NoticeGetTest {

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
    void 공지사항_목록_조회_성공() throws InterruptedException {
        for(int i=1; i<=15; i++){
            Notice.NoticeBuilder builder = Notice.builder();
            builder.userId(admin.getId())
                    .title("title - " + i)
                    .content("content - " + i);
            if(i<=10) builder.noticeStatus(NoticeStatus.OPEN);
            else builder.noticeStatus(NoticeStatus.DELETED);
            Notice notice = builder.build();

            noticeRepository.save(notice);
            Thread.sleep(1);
        }

        // 아무 것도 입력 X
        // 키워드 입력 keyword = "1", 정렬 X
        // 키워드 입력 X, 정렬 오래된 순
        // 키워드 입력 keyword = "1", 정렬 오래된 순
        SearchNoticeCondition condition1 = SearchNoticeCondition.builder()
                .build();
        SearchNoticeCondition condition2 = SearchNoticeCondition.builder()
                .keyword("1")
                .build();
        SearchNoticeCondition condition3 = SearchNoticeCondition.builder()
                .sortType(NoticeSortType.OLDEST)
                .build();
        SearchNoticeCondition condition4 = SearchNoticeCondition.builder()
                .keyword("1")
                .sortType(NoticeSortType.OLDEST)
                .build();
        List<NoticeListDto> notionList1 = noticeService.getNotionList(condition1);
        List<NoticeListDto> notionList2 = noticeService.getNotionList(condition2);
        List<NoticeListDto> notionList3 = noticeService.getNotionList(condition3);
        List<NoticeListDto> notionList4 = noticeService.getNotionList(condition4);

        assertThat(notionList1.size()).isEqualTo(10);
        assertThat(notionList2.size()).isEqualTo(2);
        assertThat(notionList3.size()).isEqualTo(10);
        assertThat(notionList4.size()).isEqualTo(2);

        for(NoticeListDto dto : notionList1){
            System.out.println(dto);
        }

        assertThat(notionList1.get(0).getTitle()).isEqualTo("title - 10");
        assertThat(notionList2.get(0).getTitle()).isEqualTo("title - 10");
        assertThat(notionList3.get(0).getTitle()).isEqualTo("title - 1");
        assertThat(notionList4.get(0).getTitle()).isEqualTo("title - 1");
    }

    @Test
    void 공지사항_세부_조회_성공() {
        Notice notice = Notice.builder()
                .userId(admin.getId())
                .title("테스트 title")
                .content("테스트 content")
                .noticeStatus(NoticeStatus.OPEN)
                .build();

        noticeRepository.save(notice);

        NoticeDetailDto notionDetail = noticeService.getNotionDetail(notice.getId());

        assertThat(notionDetail.getTitle()).isEqualTo("테스트 title");
        assertThat(notionDetail.getContent()).isEqualTo("테스트 content");
        assertThat(notionDetail.getWriterId()).isEqualTo(admin.getId());
        assertThat(notionDetail.getWriterName()).isEqualTo(admin.getName());
    }

}
