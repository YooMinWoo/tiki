package com.example.tiki.notion.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotion is a Querydsl query type for Notion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotion extends EntityPathBase<Notion> {

    private static final long serialVersionUID = -1048339931L;

    public static final QNotion notion = new QNotion("notion");

    public final com.example.tiki.global.entity.QBaseEntity _super = new com.example.tiki.global.entity.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final EnumPath<com.example.tiki.notion.domain.enums.NotionStatus> notionStatus = createEnum("notionStatus", com.example.tiki.notion.domain.enums.NotionStatus.class);

    public final StringPath title = createString("title");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QNotion(String variable) {
        super(Notion.class, forVariable(variable));
    }

    public QNotion(Path<? extends Notion> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotion(PathMetadata metadata) {
        super(Notion.class, metadata);
    }

}

