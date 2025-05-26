package com.example.tiki.match.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMatchRequest is a Querydsl query type for MatchRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchRequest extends EntityPathBase<MatchRequest> {

    private static final long serialVersionUID = -2019326018L;

    public static final QMatchRequest matchRequest = new QMatchRequest("matchRequest");

    public final com.example.tiki.global.entity.QBaseEntity _super = new com.example.tiki.global.entity.QBaseEntity(this);

    public final NumberPath<Long> applicantTeamId = createNumber("applicantTeamId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Long> matchPostId = createNumber("matchPostId", Long.class);

    public final EnumPath<com.example.tiki.match.domain.enums.RequestStatus> requestStatus = createEnum("requestStatus", com.example.tiki.match.domain.enums.RequestStatus.class);

    public QMatchRequest(String variable) {
        super(MatchRequest.class, forVariable(variable));
    }

    public QMatchRequest(Path<? extends MatchRequest> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMatchRequest(PathMetadata metadata) {
        super(MatchRequest.class, metadata);
    }

}

