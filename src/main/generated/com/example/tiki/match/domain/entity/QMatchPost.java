package com.example.tiki.match.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMatchPost is a Querydsl query type for MatchPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchPost extends EntityPathBase<MatchPost> {

    private static final long serialVersionUID = -265246255L;

    public static final QMatchPost matchPost = new QMatchPost("matchPost");

    public final com.example.tiki.global.entity.QBaseEntity _super = new com.example.tiki.global.entity.QBaseEntity(this);

    public final NumberPath<Long> applicantTeamId = createNumber("applicantTeamId", Long.class);

    public final StringPath buildingNumber = createString("buildingNumber");

    public final StringPath city = createString("city");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath detailAddress = createString("detailAddress");

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> hostTeamId = createNumber("hostTeamId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final EnumPath<com.example.tiki.match.domain.enums.MatchStatus> matchStatus = createEnum("matchStatus", com.example.tiki.match.domain.enums.MatchStatus.class);

    public final StringPath region = createString("region");

    public final StringPath roadName = createString("roadName");

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public QMatchPost(String variable) {
        super(MatchPost.class, forVariable(variable));
    }

    public QMatchPost(Path<? extends MatchPost> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMatchPost(PathMetadata metadata) {
        super(MatchPost.class, metadata);
    }

}

