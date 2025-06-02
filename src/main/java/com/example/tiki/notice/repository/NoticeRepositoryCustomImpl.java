package com.example.tiki.notice.repository;

import com.example.tiki.notice.domain.entity.Notice;
import com.example.tiki.notice.domain.entity.QNotice;
import com.example.tiki.notice.domain.enums.NoticeStatus;
import com.example.tiki.notice.dto.NoticeSortType;
import com.example.tiki.notice.dto.SearchNoticeCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notice> searchNotionList(SearchNoticeCondition condition) {
        QNotice notice = QNotice.notice;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(
                notice.noticeStatus.ne(NoticeStatus.DELETED)
        );

        if(condition.getKeyword() != null && !condition.getKeyword().isEmpty()){
            builder.and(
                    notice.title.containsIgnoreCase(condition.getKeyword())
            );
        }

        // 정렬 기준 결정
        OrderSpecifier<?> orderSpecifier = getSortOrder(condition.getSortType(), notice);

        // query 실행 (페이징 및 정렬 적용)
        return queryFactory
                .selectFrom(notice)
                .where(builder)
                .orderBy(orderSpecifier)  // startTime 이른 순 정렬
                .fetch();
    }


    private OrderSpecifier<?> getSortOrder(NoticeSortType sortType, QNotice notice) {
        if (sortType == null) {
            return notice.createdDate.desc();
        }

        return switch (sortType) {
            case LATEST -> notice.createdDate.desc();
            case OLDEST -> notice.createdDate.asc();
        };
    }
}
