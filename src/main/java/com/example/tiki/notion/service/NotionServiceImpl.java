package com.example.tiki.notion.service;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.global.exception.ForbiddenException;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.notion.domain.entity.Notion;
import com.example.tiki.notion.domain.enums.NotionStatus;
import com.example.tiki.notion.dto.CreateNotionRequest;
import com.example.tiki.notion.dto.NotionDetailDto;
import com.example.tiki.notion.dto.NotionListDto;
import com.example.tiki.notion.dto.SearchNotionCondition;
import com.example.tiki.notion.repository.NotionRepository;
import com.example.tiki.utils.CheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotionServiceImpl implements NotionService{

    private final NotionRepository notionRepository;
    private final AuthRepository authRepository;
    private final CheckUtil checkUtil;


    // 공지사항 등록
    @Override
    @Transactional
    public void createNotion(User user, CreateNotionRequest request) {
        Notion notion = Notion.builder()
                .userId(user.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .notionStatus(NotionStatus.OPEN)
                .build();

        notionRepository.save(notion);
    }

    // 공지사항 리스트 조회
    @Override
    public List<NotionListDto> getNotionList(SearchNotionCondition condition) {
        List<NotionListDto> result = new ArrayList<>();
        List<Notion> notions = notionRepository.searchNotionList(condition);
        for (Notion notion : notions) {
            result.add(NotionListDto.builder()
                            .notionId(notion.getId())
                            .title(notion.getTitle())
                            .createDate(notion.getCreatedDate())
                            .build());
        }
        return result;
    }


    // 공지사항 상세 조회
    @Override
    public NotionDetailDto getNotionDetail(Long notionId) {
        Notion notion = checkUtil.validateAndGetNotion(notionId);
        User writer = authRepository.findById(notion.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 게시물의 사용자를 찾을 수 없습니다."));
        return NotionDetailDto.builder()
                    .notionId(notion.getId())
                    .writerId(writer.getId())
                    .writerName(writer.getName())
                    .title(notion.getTitle())
                    .content(notion.getContent())
                    .createDate(notion.getCreatedDate())
                    .lastModifiedDate(notion.getLastModifiedDate())
                    .build();
    }

    // 공지사항 수정
    @Transactional
    @Override
    public void updateNotion(CreateNotionRequest request, Long notionId) {
        // 관리자 권한이라면 누구나 수정 가능
        Notion notion = checkUtil.validateAndGetNotion(notionId);
//        if(notion.getUserId() != user.getId()) throw new ForbiddenException("권한이 없습니다.");
        notion.update(request);
    }

    // 공지사항 삭제
    @Override
    @Transactional
    public void deleteNotion(Long notionId) {
        // 관리자 권한이라면 누구나 삭제 가능
        Notion notion = checkUtil.validateAndGetNotion(notionId);
        notion.delete();
    }



}
