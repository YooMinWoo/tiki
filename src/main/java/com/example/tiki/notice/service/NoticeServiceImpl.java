package com.example.tiki.notice.service;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import com.example.tiki.global.exception.NotFoundException;
import com.example.tiki.notice.domain.entity.Notice;
import com.example.tiki.notice.domain.enums.NoticeStatus;
import com.example.tiki.notice.dto.CreateNoticeRequest;
import com.example.tiki.notice.dto.NoticeDetailDto;
import com.example.tiki.notice.dto.NoticeListDto;
import com.example.tiki.notice.dto.SearchNoticeCondition;
import com.example.tiki.notice.repository.NoticeRepository;
import com.example.tiki.utils.CheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final AuthRepository authRepository;
    private final CheckUtil checkUtil;


    // 공지사항 등록
    @Override
    @Transactional
    public void createNotion(User user, CreateNoticeRequest request) {
        Notice notice = Notice.builder()
                .userId(user.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .noticeStatus(NoticeStatus.OPEN)
                .build();

        noticeRepository.save(notice);
    }

    // 공지사항 리스트 조회
    @Override
    public List<NoticeListDto> getNotionList(SearchNoticeCondition condition) {
        List<NoticeListDto> result = new ArrayList<>();
        List<Notice> notices = noticeRepository.searchNotionList(condition);
        for (Notice notice : notices) {
            result.add(NoticeListDto.builder()
                            .notionId(notice.getId())
                            .title(notice.getTitle())
                            .createDate(notice.getCreatedDate())
                            .build());
        }
        return result;
    }


    // 공지사항 상세 조회
    @Override
    public NoticeDetailDto getNotionDetail(Long notionId) {
        Notice notice = checkUtil.validateAndGetNotion(notionId);
        User writer = authRepository.findById(notice.getUserId())
                .orElseThrow(() -> new NotFoundException("해당 게시물의 사용자를 찾을 수 없습니다."));
        return NoticeDetailDto.builder()
                    .notionId(notice.getId())
                    .writerId(writer.getId())
                    .writerName(writer.getName())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .createDate(notice.getCreatedDate())
                    .lastModifiedDate(notice.getLastModifiedDate())
                    .build();
    }

    // 공지사항 수정
    @Transactional
    @Override
    public void updateNotion(CreateNoticeRequest request, Long notionId) {
        // 관리자 권한이라면 누구나 수정 가능
        Notice notice = checkUtil.validateAndGetNotion(notionId);
//        if(notion.getUserId() != user.getId()) throw new ForbiddenException("권한이 없습니다.");
        notice.update(request);
    }

    // 공지사항 삭제
    @Override
    @Transactional
    public void deleteNotion(Long notionId) {
        // 관리자 권한이라면 누구나 삭제 가능
        Notice notice = checkUtil.validateAndGetNotion(notionId);
        notice.delete();
    }



}
