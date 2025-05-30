package com.example.tiki.notion.repository;

import com.example.tiki.notion.domain.entity.Notion;
import com.example.tiki.notion.domain.entity.QNotion;
import com.example.tiki.notion.domain.enums.NotionStatus;
import com.example.tiki.notion.dto.NotionSortType;
import com.example.tiki.notion.dto.SearchNotionCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NotionRepositoryCustomImpl implements NotionRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notion> searchNotionList(SearchNotionCondition condition) {
        QNotion notion = QNotion.notion;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(
                notion.notionStatus.ne(NotionStatus.DELETED)
        );

        if(condition.getKeyword() != null && !condition.getKeyword().isEmpty()){
            builder.and(
                    notion.title.containsIgnoreCase(condition.getKeyword())
            );
        }

        // 정렬 기준 결정
        OrderSpecifier<?> orderSpecifier = getSortOrder(condition.getSortType(), notion);

        // query 실행 (페이징 및 정렬 적용)
        return queryFactory
                .selectFrom(notion)
                .where(builder)
                .orderBy(orderSpecifier)  // startTime 이른 순 정렬
                .fetch();
    }


    private OrderSpecifier<?> getSortOrder(NotionSortType sortType, QNotion notion) {
        if (sortType == null) {
            return notion.createdDate.desc();
        }

        return switch (sortType) {
            case LATEST -> notion.createdDate.desc();
            case OLDEST -> notion.createdDate.asc();
        };
    }
}
