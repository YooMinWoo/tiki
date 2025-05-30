package com.example.tiki.notion;

import com.example.tiki.auth.domain.Role;
import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.notion.domain.entity.Notion;
import com.example.tiki.notion.domain.enums.NotionStatus;
import com.example.tiki.notion.dto.*;
import com.example.tiki.notion.repository.NotionRepository;
import com.example.tiki.notion.service.NotionService;
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
public class NotionGetTest {

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
    void 공지사항_목록_조회_성공() throws InterruptedException {
        for(int i=1; i<=15; i++){
            Notion.NotionBuilder builder = Notion.builder();
            builder.userId(admin.getId())
                    .title("title - " + i)
                    .content("content - " + i);
            if(i<=10) builder.notionStatus(NotionStatus.OPEN);
            else builder.notionStatus(NotionStatus.DELETED);
            Notion notion = builder.build();

            notionRepository.save(notion);
            Thread.sleep(1);
        }

        // 아무 것도 입력 X
        // 키워드 입력 keyword = "1", 정렬 X
        // 키워드 입력 X, 정렬 오래된 순
        // 키워드 입력 keyword = "1", 정렬 오래된 순
        SearchNotionCondition condition1 = SearchNotionCondition.builder()
                .build();
        SearchNotionCondition condition2 = SearchNotionCondition.builder()
                .keyword("1")
                .build();
        SearchNotionCondition condition3 = SearchNotionCondition.builder()
                .sortType(NotionSortType.OLDEST)
                .build();
        SearchNotionCondition condition4 = SearchNotionCondition.builder()
                .keyword("1")
                .sortType(NotionSortType.OLDEST)
                .build();
        List<NotionListDto> notionList1 = notionService.getNotionList(condition1);
        List<NotionListDto> notionList2 = notionService.getNotionList(condition2);
        List<NotionListDto> notionList3 = notionService.getNotionList(condition3);
        List<NotionListDto> notionList4 = notionService.getNotionList(condition4);

        assertThat(notionList1.size()).isEqualTo(10);
        assertThat(notionList2.size()).isEqualTo(2);
        assertThat(notionList3.size()).isEqualTo(10);
        assertThat(notionList4.size()).isEqualTo(2);

        for(NotionListDto dto : notionList1){
            System.out.println(dto);
        }

        assertThat(notionList1.get(0).getTitle()).isEqualTo("title - 10");
        assertThat(notionList2.get(0).getTitle()).isEqualTo("title - 10");
        assertThat(notionList3.get(0).getTitle()).isEqualTo("title - 1");
        assertThat(notionList4.get(0).getTitle()).isEqualTo("title - 1");
    }

    @Test
    void 공지사항_세부_조회_성공() {
        Notion notion = Notion.builder()
                .userId(admin.getId())
                .title("테스트 title")
                .content("테스트 content")
                .notionStatus(NotionStatus.OPEN)
                .build();

        notionRepository.save(notion);

        NotionDetailDto notionDetail = notionService.getNotionDetail(notion.getId());

        assertThat(notionDetail.getTitle()).isEqualTo("테스트 title");
        assertThat(notionDetail.getContent()).isEqualTo("테스트 content");
        assertThat(notionDetail.getWriterId()).isEqualTo(admin.getId());
        assertThat(notionDetail.getWriterName()).isEqualTo(admin.getName());
    }

}
