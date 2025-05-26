package com.example.tiki.match.repository;

import com.example.tiki.match.domain.entity.MatchPost;
import com.example.tiki.match.domain.entity.QMatchPost;
import com.example.tiki.match.dto.MatchPostSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class MatchPostRepositoryCustomImpl implements MatchPostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MatchPost> search(MatchPostSearchCondition condition) {
        QMatchPost matchPost = QMatchPost.matchPost;

        BooleanBuilder builder = new BooleanBuilder();


        // keyword 검색 (title)
        if(condition.getKeyword() != null && !condition.getKeyword().isEmpty()){
            builder.and(
                    matchPost.title.containsIgnoreCase(condition.getKeyword())
            );
        }

        // 날짜 검색 (matchDate)
        if(condition.getMatchDate() == null) condition.setMatchDate(LocalDate.now());
        builder.and(matchPost.matchDate.eq(condition.getMatchDate()));

        // status 조건 (null이면 전체)
        if(condition.getStatus() != null){
            builder.and(
                    matchPost.matchStatus.eq(condition.getStatus().toMatchStatus())
            );
        }

        // 지역 선택 (region)
        if(condition.getRegion() != null && !condition.getRegion().isEmpty()){
            builder.and(
                    matchPost.region.eq(condition.getRegion())
            );
        }

        // query 실행 (페이징 및 정렬 적용)
        return queryFactory
                .selectFrom(matchPost)
                .where(builder)
                .orderBy(matchPost.startTime.asc())  // startTime 이른 순 정렬
                .fetch();

    }
}
