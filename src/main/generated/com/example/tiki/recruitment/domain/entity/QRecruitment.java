package com.example.tiki.recruitment.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRecruitment is a Querydsl query type for Recruitment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecruitment extends EntityPathBase<Recruitment> {

    private static final long serialVersionUID = 558640383L;

    public static final QRecruitment recruitment = new QRecruitment("recruitment");

    public final com.example.tiki.global.entity.QBaseEntity _super = new com.example.tiki.global.entity.QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> closedAt = createDateTime("closedAt", java.time.LocalDateTime.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final DateTimePath<java.time.LocalDateTime> openedAt = createDateTime("openedAt", java.time.LocalDateTime.class);

    public final EnumPath<com.example.tiki.recruitment.domain.enums.RecruitmentStatus> recruitmentStatus = createEnum("recruitmentStatus", com.example.tiki.recruitment.domain.enums.RecruitmentStatus.class);

    public final NumberPath<Long> teamId = createNumber("teamId", Long.class);

    public final StringPath title = createString("title");

    public QRecruitment(String variable) {
        super(Recruitment.class, forVariable(variable));
    }

    public QRecruitment(Path<? extends Recruitment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRecruitment(PathMetadata metadata) {
        super(Recruitment.class, metadata);
    }

}

